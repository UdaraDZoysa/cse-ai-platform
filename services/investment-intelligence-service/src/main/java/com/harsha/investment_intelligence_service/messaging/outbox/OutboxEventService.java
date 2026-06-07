package com.harsha.investment_intelligence_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.versions.EventVersions;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.messaging.dlt.DltMessage;
import com.harsha.investment_intelligence_service.messaging.dlt.DltProcessingRequested;
import com.harsha.investment_intelligence_service.messaging.dlt.DltRepository;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
public class OutboxEventService {
    private final KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate;
    private final DltRepository dltRepository;
    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(OutboxEventService.class);

    public OutboxEventService(
            KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate,
            DltRepository dltRepository, OutboxRepository outboxRepository,
            ApplicationEventPublisher eventPublisher,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.dltRepository = dltRepository;
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void publishSingleEvent(OutboxEvent event) {
        try {
            event.markProcessing();
            outboxRepository.save(event);

            EventEnvelope<JsonNode> envelope = new EventEnvelope<>(
                    event.getId().toString(),
                    event.getAggregateId(),
                    event.getEventType(),
                    EventVersions.V1,
                    "investment-intelligence-service",
                    Instant.now().toEpochMilli(),
                    event.getPayload()
            );
            kafkaTemplate.send(
                    event.getEventType().mainTopic(),
                    envelope.aggregateId(),
                    envelope
            );

            event.markProcessed();
            outboxRepository.save(event);

            log.debug(
                    "Outbox event published successfully -> id={}, aggregateId={}",
                    event.getId(),
                    event.getAggregateId()
            );
        } catch (SerializationException ex) {
            queueToDlt(event, ProcessingErrorType.INVALID_EVENT, ex);

        } catch (Exception ex) {
            handleRetry(event, ex);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    private void queueToDlt(
            OutboxEvent event,
            ProcessingErrorType errorType,
            Exception ex
    ) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event.getPayload());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }

        DltMessage dltMessage = new DltMessage(
                event.getId().toString(),
                event.getAggregateId(),
                event.getEventType(),
                event.getEventType().dltTopic(),
                payload,
                errorType,
                ex.getMessage(),
                event.getRetryCount(),
                event.getCreatedAt()
        );

        event.markDltQueued();
        outboxRepository.save(event);

        try {
            dltRepository.save(dltMessage);

            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new DltProcessingRequested())
            );

        } catch (DataIntegrityViolationException e) {
            log.debug(
                    """
                    Error during dlt persisting.
                    
                    symbol: {}
                    Reason: {}
                    
                    """,
                    event.getAggregateId(),
                    e.getMessage());
        }
    }

    private void handleRetry(
            OutboxEvent event,
            Exception ex
    ) {
        event.markAttempt();
        if (!event.shouldRetry()) {
            queueToDlt(event, ProcessingErrorType.RETRY_EXHAUSTED, ex);
        }

        long backoff = calculateBackoff(event.getRetryCount());

        Instant nextAttemptAt = Instant.now().plusSeconds(backoff);

        event.setNextAttemptAt(nextAttemptAt);

        event.markRetryScheduled();

        outboxRepository.save(event);

        afterCommitOrNow(() ->
                eventPublisher.publishEvent(new OutboxRetryScheduled(nextAttemptAt))
        );

        log.warn(
                """
                Scheduling retry Outbox Event.
    
                symbol={}
                retryCount={}
                nextAttemptAt={}
                reason={}
                """,
                event.getAggregateId(),
                event.getRetryCount(),
                event.getNextAttemptAt(),
                ex.getMessage()
        );

    }

    private void afterCommitOrNow(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            action.run();
                        }
                    }
            );
        } else {
            action.run();
        }
    }
}

package com.harsha.market_intelligence_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.versions.EventVersions;
import com.harsha.market_intelligence_service.exception.ProcessingErrorType;
import com.harsha.market_intelligence_service.messaging.dlt.DltMessage;
import com.harsha.market_intelligence_service.messaging.dlt.DltProcessingRequested;
import com.harsha.market_intelligence_service.messaging.dlt.DltProcessor;
import com.harsha.market_intelligence_service.messaging.dlt.DltRepository;
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
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OutboxEventService {
    private final KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate;
    private final DltRepository dltRepository;
    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(OutboxEventService.class);

    public OutboxEventService(
            KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate,
            DltRepository dltRepository,
            OutboxRepository outboxRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.dltRepository = dltRepository;
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
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
                    "market-intelligence-service",
                    Instant.now().toEpochMilli(),
                    event.getPayload()
            );
            kafkaTemplate.send(
                    event.getEventType().mainTopic(),
                    envelope.aggregateId(),
                    envelope).get();

            event.markProcessed();
            outboxRepository.save(event);

            log.debug(
                    "Outbox event published successfully -> id={}, aggregateId={}",
                    event.getId(),
                    event.getAggregateId()
            );

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            handleRetry(event, ex);

        } catch (SerializationException ex) {
            queueToDlt(event, ProcessingErrorType.INVALID_EVENT, ex);

        } catch (Exception ex) {
            handleRetry(event, ex);
        }
    }
    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(
                300000,
                Math.pow(2, retryCount) * 1000
        );

        double jitter = ThreadLocalRandom.current()
                .nextDouble(0.8, 1.2);

        return (long) (baseDelay * jitter);
    }

    private void queueToDlt(
            OutboxEvent event,
            ProcessingErrorType errorType,
            Exception ex
    ) {
        DltMessage dltMessage = DltMessage.builder()
                .id(event.getId().toString())
                .aggregateId(event.getAggregateId())
                .eventType(event.getEventType())
                .payload(event.getPayload())
                .targetTopic(event.getEventType().dltTopic())
                .errorType(errorType)
                .errorMessage(ex.getMessage())
                .originRetryCount(event.getRetryCount())
                .originCreatedAt(event.getCreatedAt())
                .build();

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
            return;
        }

        long backoff = calculateBackoff(event.getRetryCount());

        Instant nextAttemptAt = Instant.now().plusMillis(backoff);

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

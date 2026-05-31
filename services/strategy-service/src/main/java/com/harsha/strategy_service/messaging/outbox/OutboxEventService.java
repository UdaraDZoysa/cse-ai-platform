package com.harsha.strategy_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.versions.EventVersions;
import com.harsha.strategy_service.exception.DltErrorType;
import com.harsha.strategy_service.messaging.dlt.DltMessage;
import com.harsha.strategy_service.messaging.dlt.DltRepository;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Service
public class OutboxEventService {
    private final KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate;
    private final DltRepository dltRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(OutboxEventService.class);

    public OutboxEventService(
            KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate,
            DltRepository dltRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.dltRepository = dltRepository;
        this.outboxRepository = outboxRepository;
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
                    "strategy-service",
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

        } catch (KafkaException | ExecutionException ex) {
            handleRetry(event, DltErrorType.RETRY_EXHAUSTED, ex);

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            handleRetry(event, DltErrorType.RETRY_EXHAUSTED, ex);

        } catch (SerializationException ex) {
            queueToDlt(event, DltErrorType.INVALID_EVENT, ex);

        } catch (Exception ex) {
            handleRetry(event, DltErrorType.UNKNOWN, ex);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    public void queueToDlt(
            OutboxEvent event,
            DltErrorType errorType,
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
            DltErrorType finalErrorType,
            Exception ex
    ) {
        event.markAttempt();
        if (!event.shouldRetry()) {
            queueToDlt(event, finalErrorType, ex);
            return;
        }

        long backoff = calculateBackoff(event.getRetryCount());

        event.setNextAttemptAt(
                Instant.now().plusMillis(backoff)
        );

        event.markRetryScheduled();

        outboxRepository.save(event);
    }
}

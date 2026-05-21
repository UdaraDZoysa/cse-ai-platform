package com.harsha.analysis_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.harsha.analysis_service.exception.DltErrorType;
import com.harsha.analysis_service.messaging.dlt.DltMessage;
import com.harsha.analysis_service.messaging.dlt.DltRepository;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.versions.EventVersions;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class OutboxEventService {
    private final KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate;
    private final DltRepository dltRepository;
    private final OutboxRepository outboxRepository;

    public OutboxEventService(
            KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate,
            DltRepository dltRepository,
            OutboxRepository outboxRepository
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.dltRepository = dltRepository;
        this.outboxRepository = outboxRepository;
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
                    "analysis-service",
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
        try {
            DltMessage dltMessage = new DltMessage(
                    event.getId().toString(),
                    event.getAggregateId(),
                    event.getEventType(),
                    event.getEventType().dltTopic(),
                    event.getPayload(),
                    errorType,
                    ex.getMessage(),
                    event.getRetryCount(),
                    event.getCreatedAt()
            );

            event.markDltQueued();
            dltRepository.save(dltMessage);

            try {
                dltRepository.save(dltMessage);
            } catch (DataIntegrityViolationException e) {
                // duplicate --> ignore
            }

        } catch (Exception sendEx) {
            log.error("Failed to save in DltMessage → id={}, reason={}",
                    event.getId(),
                    sendEx.getMessage());
        }
    }

    private void handleRetry(
            OutboxEvent event,
            DltErrorType finalErrorType,
            Exception ex
    ) {
        long backoff = calculateBackoff(event.getRetryCount());

        if (event.getLastAttemptAt() != null
                && Instant.now().isBefore(event.getLastAttemptAt().plusMillis(backoff))
        ) {
            return;
        }
        event.markAttempt();

        if (!event.shouldRetry()) {
            queueToDlt(event, finalErrorType, ex);
        } else {
            event.markPending();
            outboxRepository.save(event);
        }
    }
}

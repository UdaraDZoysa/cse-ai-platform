package com.harsha.analysis_service.outbox;

import com.harsha.analysis_service.exception.DltErrorType;
import com.harsha.contracts.messaging.DltEventEnvelope;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.versions.EventVersions;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Slf4j
public class OutboxEventService {
    private final KafkaTemplate<String, EventEnvelope<String>> kafkaTemplate;
    private final KafkaTemplate<String, DltEventEnvelope> dltKafkaTemplate;
    private final OutboxRepository outboxRepository;

    public OutboxEventService(
            KafkaTemplate<String, EventEnvelope<String>> kafkaTemplate,
            KafkaTemplate<String, DltEventEnvelope> dltKafkaTemplate,
            OutboxRepository outboxRepository
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.dltKafkaTemplate = dltKafkaTemplate;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void publishSingleEvent(OutboxEvent event) {
        try {
            event.markProcessing();
            outboxRepository.save(event);

            EventEnvelope<String> envelope = new EventEnvelope<String>(
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
            sendToDlt(event, DltErrorType.INVALID_EVENT, ex);

        } catch (Exception ex) {
            handleRetry(event, DltErrorType.UNKNOWN, ex);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    public void sendToDlt(
            OutboxEvent event,
            DltErrorType errorType,
            Exception ex
    ) {
        try {
            DltEventEnvelope dltEnvelope = new DltEventEnvelope(
                    event.getId().toString(),
                    event.getAggregateId(),
                    event.getEventType(),
                    "analysis-service",
                    event.getCreatedAt().toEpochMilli(),
                    event.getPayload(),
                    errorType.name(),
                    ex.getMessage(),
                    event.getRetryCount(),
                    System.currentTimeMillis()
            );

            event.markFailed();
            outboxRepository.save(event);

            dltKafkaTemplate.send(
                    event.getEventType().dltTopic(),
                    event.getAggregateId(),
                    dltEnvelope
            ).get();

            log.debug(
                    "DLT published successfully -> eventId={}",
                    event.getId()
            );

        } catch (Exception sendEx) {
            log.error("Failed to publish outbox event to DLT → id={}, reason={}",
                    event.getId(),
                    sendEx.getMessage());

            event.markAttempt();
            event.markPending();
            outboxRepository.save(event);
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
            sendToDlt(event, finalErrorType, ex);
        } else {
            event.markPending();
            outboxRepository.save(event);
        }
    }
}

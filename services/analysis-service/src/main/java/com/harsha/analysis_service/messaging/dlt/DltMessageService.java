package com.harsha.analysis_service.messaging.dlt;

import com.fasterxml.jackson.databind.JsonNode;
import com.harsha.contracts.messaging.DltEventEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class DltMessageService {
    private final DltRepository dltRepository;
    private final KafkaTemplate<String, DltEventEnvelope<JsonNode>> kafkaTemplate;

    public DltMessageService(
            DltRepository dltRepository,
            KafkaTemplate<String, DltEventEnvelope<JsonNode>> kafkaTemplate) {
        this.dltRepository = dltRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void processSingleMessage(DltMessage message) {
        try {
            message.markProcessing();
            dltRepository.save(message);

            DltEventEnvelope<JsonNode> dltEnvelope = new DltEventEnvelope<>(
                    message.getId(),
                    message.getAggregateId(),
                    message.getEventType(),
                    "analysis-service",
                    message.getOriginCreatedAt().toEpochMilli(),
                    message.getPayload(),
                    message.getErrorType().name(),
                    message.getErrorMessage(),
                    message.getOriginRetryCount(),
                    message.getDltCreatedAt().toEpochMilli()
            );

            kafkaTemplate.send(message.getTargetTopic(), message.getAggregateId(), dltEnvelope).
                    get();

            message.markPublished();
            dltRepository.save(message);
            log.debug(
                    "DLT published successfully -> messageId={}",
                    message.getId()
            );

        } catch (Exception e) {
            handleRetry(message, e.getMessage());
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    private void handleRetry(
            DltMessage message,
            String errorMessage
    ) {
        long backoff = calculateBackoff(message.getDltRetryCount());

        if (message.getLastAttemptAt() != null
                && Instant.now().isBefore(message.getLastAttemptAt().plusMillis(backoff))
        ) {
            return;
        }
        message.markAttempt();

        if (!message.shouldRetry()) {
            message.markFailed(errorMessage);
            log.debug(
                    "DLT failed to publish -> messageId={}",
                    message.getId()
            );
        } else {
            message.markPending();
        }
        dltRepository.save(message);
    }
}

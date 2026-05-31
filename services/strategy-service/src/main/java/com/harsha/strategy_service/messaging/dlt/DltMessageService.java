package com.harsha.strategy_service.messaging.dlt;

import com.harsha.contracts.messaging.DltEventEnvelope;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class DltMessageService {
    private final DltRepository dltRepository;
    private final KafkaTemplate<String, DltEventEnvelope<String>> kafkaTemplate;

    public DltMessageService(
            DltRepository dltRepository,
            KafkaTemplate<String, DltEventEnvelope<String>> kafkaTemplate) {
        this.dltRepository = dltRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void processSingleMessage(DltMessage message) {
        try {
            message.markProcessing();
            dltRepository.save(message);

            DltEventEnvelope<String> dltEnvelope = new DltEventEnvelope<>(
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

            kafkaTemplate.send(
                            message.getTargetTopic(),
                            message.getAggregateId(),
                            dltEnvelope
                    ).get();

            message.markPublished();
            dltRepository.save(message);
            log.debug(
                    """
                    
                    DLT published successfully.
                    
                    messageId={}
                    symbol={}
                    
                    """,
                    message.getId(),
                    message.getAggregateId()
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
        message.markAttempt();

        if (!message.shouldRetry()) {
            message.markFailed(errorMessage);
            dltRepository.save(message);
            log.debug(
                    """ 
                    DLT failed to publish.
                    
                    Dlt messageId={}
                    Symbol={}
                    retryCount={}
                    
                    """,
                    message.getId(),
                    message.getAggregateId(),
                    message.getDltRetryCount()
            );
            return;
        }

        long backoff = calculateBackoff(message.getDltRetryCount());

        message.setNextAttemptAt(
                Instant.now().plusSeconds(backoff)
        );

        message.markRetryScheduled();

        dltRepository.save(message);
    }
}

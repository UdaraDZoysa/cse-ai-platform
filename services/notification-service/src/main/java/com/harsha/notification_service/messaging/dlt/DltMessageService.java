package com.harsha.notification_service.messaging.dlt;
import com.harsha.contracts.messaging.DltEventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
public class DltMessageService {
    private final DltRepository dltRepository;
    private final KafkaTemplate<String, DltEventEnvelope<String>> kafkaTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(DltMessageService.class);

    public DltMessageService(
            DltRepository dltRepository,
            KafkaTemplate<String, DltEventEnvelope<String>> kafkaTemplate,
            ApplicationEventPublisher eventPublisher
    ) {
        this.dltRepository = dltRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processSingleMessage(DltMessage message) {
        try {
            message.markProcessing();
            dltRepository.save(message);

            DltEventEnvelope<String> dltEventEnvelope = new DltEventEnvelope<>(
                    message.getId(),
                    message.getAggregateId(),
                    message.getEventType(),
                    "notification-service",
                    message.getOriginCreatedAt().toEpochMilli(),
                    message.getPayload(),
                    message.getErrorType().name(),
                    message.getErrorMessage(),
                    message.getOriginRetryCount(),
                    message.getDltCreatedAt().toEpochMilli()
            );

            kafkaTemplate
                    .send(
                            message.getEventType().dltTopic(),
                            message.getAggregateId(),
                            dltEventEnvelope
                    )
                    .get();
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

        Instant nextAttemptAt = Instant.now().plusMillis(backoff);

        message.setNextAttemptAt(nextAttemptAt);

        message.markRetryScheduled();

        dltRepository.save(message);

        afterCommitOrNow(() ->
                eventPublisher.publishEvent(
                        new DltRetryScheduled(nextAttemptAt))
        );

        log.debug(
                """
                Scheduling dlt retry.
    
                symbol={}
                retryCount={}
                nextAttemptAt={}
                reason={}
                """,
                message.getAggregateId(),
                message.getDltRetryCount(),
                message.getNextAttemptAt(),
                errorMessage
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

package com.harsha.analysis_service.inbox;

import com.harsha.analysis_service.dispatcher.EventDispatcher;
import com.harsha.analysis_service.exception.DltErrorType;
import com.harsha.analysis_service.exception.InvalidEventException;
import com.harsha.analysis_service.exception.NonRetryableProcessingException;
import com.harsha.analysis_service.exception.RetryableProcessingException;
import com.harsha.contracts.messaging.DltEventEnvelope;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InboxEventService {
    private final EventDispatcher eventDispatcher;
    private final KafkaTemplate<String, DltEventEnvelope> kafkaTemplate;
    private final InboxRepository inboxRepository;
    private static final Logger log = LoggerFactory.getLogger(InboxEventService.class);

    public InboxEventService(
            EventDispatcher eventDispatcher,
            InboxRepository inboxRepository,
            KafkaTemplate<String, DltEventEnvelope> kafkaTemplate
    ) {
        this.eventDispatcher = eventDispatcher;
        this.inboxRepository = inboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void processSingleEvent(InboxEvent event) {
        try {
            event.markProcessing();
            inboxRepository.save(event);
            eventDispatcher.dispatch(event);
            event.markProcessed();
            inboxRepository.save(event);
        } catch (InvalidEventException ex) {
            sendToDlt(event, DltErrorType.INVALID_EVENT, ex);

        } catch (RetryableProcessingException ex) {
            handleRetry(event, ex, DltErrorType.RETRY_EXHAUSTED);

        } catch (NonRetryableProcessingException ex) {
            sendToDlt(event, DltErrorType.NON_RETRYABLE, ex);

        } catch (Exception ex) {
            handleRetry(event, ex, DltErrorType.UNKNOWN);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    private void sendToDlt(InboxEvent event,DltErrorType errorType, Exception ex) {
        try {
            DltEventEnvelope dltEnvelope = new DltEventEnvelope(
                    event.getId(),
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
            inboxRepository.save(event);

            kafkaTemplate.send(
                    event.getEventType().dltTopic(),
                    event.getAggregateId(),
                    dltEnvelope
            ).get();

            log.debug(
                    "DLT published successfully -> eventId={}",
                    event.getId()
            );

        } catch (Exception sendEx) {
            log.error("Failed to publish inbox event to DLT → id={}, reason={}",
                    event.getId(),
                    sendEx.getMessage());

            event.markAttempt();
            event.markPending();
            inboxRepository.save(event);
        }
    }

    private void handleRetry(InboxEvent event, Exception ex, DltErrorType finalErrorType) {
        long backoff = calculateBackoff(event.getRetryCount());

        if (event.getLastAttemptAt() != null &&
                Instant.now().isBefore(event.getLastAttemptAt().plusMillis(backoff))) {
            return;
        }

        event.markAttempt();

        if (!event.shouldRetry()){
            sendToDlt(event, finalErrorType, ex);
        } else {
            event.markPending();
            inboxRepository.save(event);
        }
    }
}

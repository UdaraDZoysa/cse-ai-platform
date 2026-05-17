package com.harsha.analysis_service.inbox;

import com.harsha.analysis_service.dispatcher.EventDispatcher;
import com.harsha.analysis_service.exception.DltErrorType;
import com.harsha.analysis_service.exception.InvalidEventException;
import com.harsha.analysis_service.exception.NonRetryableProcessingException;
import com.harsha.analysis_service.exception.RetryableProcessingException;
import com.harsha.analysis_service.messaging.dlt.DltMessage;
import com.harsha.analysis_service.messaging.dlt.DltRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InboxEventService {
    private final EventDispatcher eventDispatcher;
    private final DltRepository dltRepository;
    private final InboxRepository inboxRepository;
    private static final Logger log = LoggerFactory.getLogger(InboxEventService.class);

    public InboxEventService(
            EventDispatcher eventDispatcher,
            InboxRepository inboxRepository,
            DltRepository dltRepository
    ) {
        this.eventDispatcher = eventDispatcher;
        this.inboxRepository = inboxRepository;
        this.dltRepository = dltRepository;
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
            queueToDlt(event, DltErrorType.INVALID_EVENT, ex);

        } catch (RetryableProcessingException ex) {
            handleRetry(event, ex, DltErrorType.RETRY_EXHAUSTED);

        } catch (NonRetryableProcessingException ex) {
            queueToDlt(event, DltErrorType.NON_RETRYABLE, ex);

        } catch (Exception ex) {
            handleRetry(event, ex, DltErrorType.UNKNOWN);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    private void queueToDlt(InboxEvent event,DltErrorType errorType, Exception ex) {
        try {
            DltMessage dltMessage = new DltMessage(
                    event.getId(),
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
            inboxRepository.save(event);

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

    private void handleRetry(InboxEvent event, Exception ex, DltErrorType finalErrorType) {
        long backoff = calculateBackoff(event.getRetryCount());

        if (event.getLastAttemptAt() != null &&
                Instant.now().isBefore(event.getLastAttemptAt().plusMillis(backoff))) {
            return;
        }

        event.markAttempt();

        if (!event.shouldRetry()){
            queueToDlt(event, finalErrorType, ex);
        } else {
            event.markPending();
            inboxRepository.save(event);
        }
    }
}

package com.harsha.investment_intelligence_service.messaging.inbox;

import com.harsha.investment_intelligence_service.dispatcher.EventDispatcher;
import com.harsha.investment_intelligence_service.exception.DltErrorType;
import com.harsha.investment_intelligence_service.exception.InvalidEventException;
import com.harsha.investment_intelligence_service.exception.NonRetryableProcessingException;
import com.harsha.investment_intelligence_service.exception.RetryableProcessingException;
import com.harsha.investment_intelligence_service.messaging.dlt.DltMessage;
import com.harsha.investment_intelligence_service.messaging.dlt.DltProcessingRequested;
import com.harsha.investment_intelligence_service.messaging.dlt.DltRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
public class InboxEventService {
    private final EventDispatcher eventDispatcher;
    private final DltRepository dltRepository;
    private final InboxRepository inboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(InboxEventService.class);

    public InboxEventService(
            EventDispatcher eventDispatcher,
            DltRepository dltRepository,
            InboxRepository inboxRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.eventDispatcher = eventDispatcher;
        this.dltRepository = dltRepository;
        this.inboxRepository = inboxRepository;
        this.eventPublisher = eventPublisher;
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
            handleRetry(event, DltErrorType.RETRY_EXHAUSTED, ex);

        } catch (NonRetryableProcessingException ex) {
            queueToDlt(event, DltErrorType.NON_RETRYABLE, ex);

        } catch (Exception ex) {
            handleRetry(event, DltErrorType.UNKNOWN, ex);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    private void queueToDlt(
            InboxEvent event,
            DltErrorType errorType,
            Exception ex
    ) {
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
            InboxEvent event,
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

        inboxRepository.save(event);

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

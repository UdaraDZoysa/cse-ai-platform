package com.harsha.notification_service.application.service.notification;

import com.harsha.notification_service.application.service.sender.NotificationSender;
import com.harsha.notification_service.application.service.sender.NotificationSenderRegistry;
import com.harsha.notification_service.domain.entity.NotificationJob;
import com.harsha.notification_service.domain.model.NotificationJobRetryScheduled;
import com.harsha.notification_service.domain.repository.NotificationJobRepository;
import com.harsha.notification_service.exception.NonRetryableProcessingException;
import com.harsha.notification_service.exception.ProcessingErrorType;
import com.harsha.notification_service.exception.RetryableProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
public class NotificationJobService {
    private final NotificationJobRepository notificationJobRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationSenderRegistry senderRegistry;
    private static final Logger log = LoggerFactory.getLogger(NotificationJobService.class);


    public NotificationJobService(
            NotificationJobRepository notificationJobRepository,
            ApplicationEventPublisher eventPublisher,
            NotificationSenderRegistry senderRegistry
    ) {
        this.notificationJobRepository = notificationJobRepository;
        this.eventPublisher = eventPublisher;
        this.senderRegistry = senderRegistry;
    }

    @Transactional
    public void processJob(NotificationJob job) {
        try {
            job.markProcessing();
            notificationJobRepository.save(job);

            NotificationSender sender = senderRegistry.getSender(
                    job.getChannel()
            );

            sender.send(
                    job.getMessage()
            );

            job.markProcessed();
            notificationJobRepository.save(job);

            log.info(
                    """
                    Notification sent.

                    symbol={}
                    channel={}
                    priority={}
                    """,
                    job.getSymbol(),
                    job.getChannel(),
                    job.getPriority()
            );


        } catch (RetryableProcessingException ex) {
            handleRetry(job, ex.getErrorType(), ex);

        } catch (NonRetryableProcessingException ex) {
            markProcessFailed(job, ex.getErrorType(), ex);

        }
        catch (Exception ex) {
            handleRetry(job, ProcessingErrorType.UNKNOWN, ex);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    private void markProcessFailed(
            NotificationJob job,
            ProcessingErrorType errorType,
            Exception ex
    ) {
        job.markFailed();
        job.setErrorMessage(ex.getMessage());
        job.setErrorType(errorType);
        notificationJobRepository.save(job);

        log.error(
                """
                Notification Sending Job failed.
    
                symbol={}
                errorType={}
                retryCount={}
                reason={}
                """,
                job.getSymbol(),
                errorType,
                job.getRetryCount(),
                ex.getMessage(),
                ex
        );
    }

    private void handleRetry(
            NotificationJob job,
            ProcessingErrorType errorType,
            Exception ex
    ) {

        job.markAttempt();

        if (!job.shouldRetry()) {
            markProcessFailed(job, ProcessingErrorType.RETRY_EXHAUSTED, ex);
            return;
        }

        long backoff = calculateBackoff(job.getRetryCount());

        Instant nextAttemptAt = Instant.now().plusMillis(backoff);

        job.setNextAttemptAt(nextAttemptAt);

        job.setErrorType(errorType);

        job.setErrorMessage(ex.getMessage());

        job.markRetryScheduled();

        notificationJobRepository.save(job);

        afterCommitOrNow(() ->
                eventPublisher.publishEvent(new NotificationJobRetryScheduled(nextAttemptAt))
        );

        log.warn(
                """
                Scheduling retry Notification Sending Job.
    
                symbol={}
                retryCount={}
                nextAttemptAt={}
                reason={}
                """,
                job.getSymbol(),
                job.getRetryCount(),
                job.getNextAttemptAt(),
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

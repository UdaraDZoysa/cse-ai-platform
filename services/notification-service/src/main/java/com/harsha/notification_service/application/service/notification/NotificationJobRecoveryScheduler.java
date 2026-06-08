package com.harsha.notification_service.application.service.notification;

import com.harsha.notification_service.domain.entity.NotificationJob;
import com.harsha.notification_service.domain.model.NotificationJobProcessingRequested;
import com.harsha.notification_service.domain.model.NotificationJobRetryScheduled;
import com.harsha.notification_service.domain.repository.NotificationJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;

@Component
public class NotificationJobRecoveryScheduler {
    private final NotificationJobRepository notificationJobRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(NotificationJobRecoveryScheduler.class);

    public NotificationJobRecoveryScheduler(
            NotificationJobRepository notificationJobRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.notificationJobRepository = notificationJobRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<NotificationJob> stuckJobs =
                notificationJobRepository.findStuckProcessingEvents(cutoff);

        for (NotificationJob stuckJob : stuckJobs) {
            log.info(
                    "Recovering stuck job → id={}, processingStartedAt={}",
                    stuckJob.getId(),
                    stuckJob.getUpdatedAt()
            );
            stuckJob.setNextAttemptAt(Instant.now());
            stuckJob.markRetryScheduled();
            stuckJob.setErrorMessage(null);
            stuckJob.setErrorType(null);
            notificationJobRepository.save(stuckJob);
        }
        if (!stuckJobs.isEmpty()) {
            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(
                            new NotificationJobRetryScheduled(Instant.now())
                    )
            );
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void recover() {
        eventPublisher.publishEvent(
                new NotificationJobProcessingRequested()
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

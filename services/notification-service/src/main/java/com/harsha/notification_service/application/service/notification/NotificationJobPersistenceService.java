package com.harsha.notification_service.application.service.notification;

import com.harsha.notification_service.domain.entity.NotificationJob;
import com.harsha.notification_service.domain.model.NotificationChannel;
import com.harsha.notification_service.domain.model.NotificationJobProcessingRequested;
import com.harsha.notification_service.domain.model.NotificationMessage;
import com.harsha.notification_service.domain.repository.NotificationJobRepository;
import com.harsha.notification_service.exception.ProcessingErrorType;
import com.harsha.notification_service.exception.RetryableProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class NotificationJobPersistenceService {
    private final NotificationJobRepository notificationJobRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(NotificationJobPersistenceService.class);

    public NotificationJobPersistenceService(
            NotificationJobRepository notificationJobRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.notificationJobRepository = notificationJobRepository;
        this.eventPublisher = eventPublisher;
    }

    public void persistJob(
            NotificationMessage message,
            NotificationChannel channel
    ) {
        NotificationJob notificationJob = NotificationJob.builder()
                .id(UUID.randomUUID().toString())
                .symbol(message.symbol())
                .priority(message.priority())
                .channel(channel)
                .message(message)
                .build();

        try {
            notificationJobRepository.save(notificationJob);

            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new NotificationJobProcessingRequested())
            );

            log.info(
                    "Notification job created. symbol={}, hash={}",
                    message.symbol(),
                    message.body()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to persist Notification job. symbol={}",
                    message.symbol(),
                    e
            );

            throw new RetryableProcessingException(
                    "Failed to persist Notification job",
                    ProcessingErrorType.DATABASE_ERROR,
                    e
            );
        }
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

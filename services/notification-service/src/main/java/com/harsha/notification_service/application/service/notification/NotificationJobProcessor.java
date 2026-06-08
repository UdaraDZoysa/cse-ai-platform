package com.harsha.notification_service.application.service.notification;

import com.harsha.notification_service.domain.entity.NotificationJob;
import com.harsha.notification_service.domain.model.NotificationJobProcessingRequested;
import com.harsha.notification_service.domain.repository.NotificationJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class NotificationJobProcessor {
    private final NotificationJobRepository repository;
    private final NotificationJobService notificationJobService;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(NotificationJobProcessor.class);

    public NotificationJobProcessor(
            NotificationJobRepository repository,
            NotificationJobService notificationJobService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.notificationJobService = notificationJobService;
        this.eventPublisher = eventPublisher;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            while (true) {
                List<NotificationJob> jobs = repository.lockNextBatch();

                if (jobs.isEmpty()) {
                    break;
                }

                for (NotificationJob job : jobs) {
                    try {
                        notificationJobService.processJob(job);
                    } catch (Exception ex) {
                        log.error(
                                """
                                Unexpected job failure.
                                
                                jobId={}
                                symbol={}
                                reason={}
                                """,
                                job.getId(),
                                job.getSymbol(),
                                ex.getMessage(),
                                ex
                        );
                    }
                }
            }

        } finally {
            processing.set(false);
            if (repository.existsByPendingJob(Instant.now()) > 0) {
                eventPublisher.publishEvent(
                        new NotificationJobProcessingRequested()
                );
            }
        }
    }
}

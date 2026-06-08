package com.harsha.notification_service.application.service.notification;

import com.harsha.notification_service.domain.model.NotificationJobProcessingRequested;
import com.harsha.notification_service.domain.model.NotificationJobRetryScheduled;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NotificationJobProcessListener {
    private final NotificationJobProcessor notificationJobProcessor;
    private final TaskScheduler taskScheduler;


    public NotificationJobProcessListener(
            NotificationJobProcessor notificationJobProcessor,
            TaskScheduler taskScheduler
    ) {
        this.notificationJobProcessor = notificationJobProcessor;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void onProcessingRequest(NotificationJobProcessingRequested event) {
        taskScheduler.schedule(
                notificationJobProcessor::process,
                Instant.now().plusMillis(10)
        );
    }

    @EventListener
    public void onRetryScheduled(NotificationJobRetryScheduled event) {
        taskScheduler.schedule(
                notificationJobProcessor::process,
                event.nextAttemptAt().plusSeconds(1)
        );
    }

}

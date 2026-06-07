package com.harsha.notification_service.messaging.inbox;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InboxProcessListener {
    private final InboxProcessor inboxProcessor;
    private final TaskScheduler taskScheduler;

    public InboxProcessListener(
            InboxProcessor inboxProcessor,
            TaskScheduler taskScheduler
    ) {
        this.inboxProcessor = inboxProcessor;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void onProcessingRequested(InboxProcessingRequested event) {
        taskScheduler.schedule(
                inboxProcessor::process,
                Instant.now().plusMillis(100)
        );
    }

    @EventListener
    public void onRetryScheduled(InboxRetryScheduled event) {
        taskScheduler.schedule(
                inboxProcessor::process,
                event.nextAttemptAt().plusSeconds(1)
        );
    }
}

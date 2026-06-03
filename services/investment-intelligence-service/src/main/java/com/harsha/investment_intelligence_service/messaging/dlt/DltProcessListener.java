package com.harsha.investment_intelligence_service.messaging.dlt;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DltProcessListener {
    private final DltProcessor dltProcessor;
    private final TaskScheduler taskScheduler;

    public DltProcessListener(
            DltProcessor dltProcessor,
            TaskScheduler taskScheduler
    ) {
        this.dltProcessor = dltProcessor;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void onProcessingRequested(DltProcessingRequested event) {
        taskScheduler.schedule(
                dltProcessor::process,
                Instant.now().plusMillis(100)
        );
    }

    @EventListener
    public void onRetryScheduled(DltRetryScheduled event) {
        taskScheduler.schedule(
                dltProcessor::process,
                event.nextAttemptAt().plusSeconds(1)
        );
    }

}

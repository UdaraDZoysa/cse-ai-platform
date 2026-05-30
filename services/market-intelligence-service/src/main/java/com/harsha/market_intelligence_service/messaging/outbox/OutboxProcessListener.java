package com.harsha.market_intelligence_service.messaging.outbox;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class OutboxProcessListener {
    private final OutboxProcessor outboxProcessor;
    private final TaskScheduler taskScheduler;

    public OutboxProcessListener(
            OutboxProcessor outboxProcessor,
            TaskScheduler taskScheduler
    ) {
        this.outboxProcessor = outboxProcessor;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void onProcessingRequested(OutboxProcessingRequested event) {
        outboxProcessor.process();
    }

    @EventListener
    public void onRetryScheduled(OutboxRetryScheduled event) {
        taskScheduler.schedule(
                outboxProcessor::process,
                event.nextAttemptAt().plusSeconds(1)
        );
    }
}

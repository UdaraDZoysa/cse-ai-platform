package com.harsha.market_intelligence_service.application.insight.service.insightGenJob;

import com.harsha.market_intelligence_service.domain.insight.model.InsightGenJobProcessingRequested;
import com.harsha.market_intelligence_service.domain.insight.model.InsightGenJobRetryScheduled;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InsightGenJobListener {
    private final InsightGenJobProcessor insightGenJobProcessor;
    private final TaskScheduler taskScheduler;


    public InsightGenJobListener(
            InsightGenJobProcessor insightGenJobProcessor,
            TaskScheduler taskScheduler
    ) {
        this.insightGenJobProcessor = insightGenJobProcessor;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void onProcessingRequested(InsightGenJobProcessingRequested event) {
        taskScheduler.schedule(
                insightGenJobProcessor::process,
                Instant.now().plusMillis(100)
        );
    }

    @EventListener
    public void onRetryScheduled(InsightGenJobRetryScheduled event) {
        taskScheduler.schedule(
                insightGenJobProcessor::process,
                event.nextAttemptAt().plusSeconds(1)
        );
    }
}

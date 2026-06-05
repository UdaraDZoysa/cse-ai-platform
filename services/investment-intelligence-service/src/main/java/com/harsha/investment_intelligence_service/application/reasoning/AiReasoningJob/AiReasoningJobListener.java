package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import com.harsha.investment_intelligence_service.domain.model.reasoning.AiReasoningJobProcessingRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AiReasoningJobRetryScheduled;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AiReasoningJobListener {
    private final AiReasoningJobProcessor aiReasoningJobProcessor;
    private final TaskScheduler taskScheduler;


    public AiReasoningJobListener(
            AiReasoningJobProcessor aiReasoningJobProcessor,
            TaskScheduler taskScheduler
    ) {
        this.aiReasoningJobProcessor = aiReasoningJobProcessor;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void onProcessingRequest(AiReasoningJobProcessingRequest event) {
        taskScheduler.schedule(
                aiReasoningJobProcessor::process,
                Instant.now().plusMillis(10)
        );
    }

    @EventListener
    public void onRetryScheduled(AiReasoningJobRetryScheduled event) {
        taskScheduler.schedule(
                aiReasoningJobProcessor::process,
                event.nextAttemptAt().plusSeconds(1)
        );
    }
}

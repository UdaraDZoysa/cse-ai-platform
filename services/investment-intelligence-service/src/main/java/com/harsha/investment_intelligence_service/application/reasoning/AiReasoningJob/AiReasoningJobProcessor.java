package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AiReasoningJobProcessor {
    private final AiReasoningJobRepository repository;
    private final AiReasoningJobService aiReasoningJobService;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(AiReasoningJobProcessor.class);

    public AiReasoningJobProcessor(AiReasoningJobRepository repository, AiReasoningJobService aiReasoningJobService) {
        this.repository = repository;
        this.aiReasoningJobService = aiReasoningJobService;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            List<AiReasoningJob> jobs = repository.lockNextBatch();

            for (AiReasoningJob job : jobs) {
                try {
                    aiReasoningJobService.processJob(job);
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

        } finally {
            processing.set(false);
        }
    }
}

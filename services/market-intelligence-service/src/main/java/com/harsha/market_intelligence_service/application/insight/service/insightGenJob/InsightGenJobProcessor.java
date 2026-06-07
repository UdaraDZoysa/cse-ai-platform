package com.harsha.market_intelligence_service.application.insight.service.insightGenJob;

import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.model.InsightGenJobProcessingRequested;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class InsightGenJobProcessor {
    private final InsightGenJobRepository insightGenJobRepository;
    private final InsightGenJobService insightGenJobService;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobProcessor.class);

    public InsightGenJobProcessor(
            InsightGenJobRepository insightGenJobRepository,
            InsightGenJobService insightGenJobService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.insightGenJobRepository = insightGenJobRepository;
        this.insightGenJobService = insightGenJobService;
        this.eventPublisher = eventPublisher;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            while(true) {
                List<InsightGenerationJob> jobs = insightGenJobRepository.lockNextBatch();

                if (jobs.isEmpty()) {
                    break;
                }

                for (InsightGenerationJob job : jobs) {
                    try {
                        insightGenJobService.processJob(job);
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
            if (insightGenJobRepository.existsByPendingJob(Instant.now()) > 0) {
                eventPublisher.publishEvent(
                        new InsightGenJobProcessingRequested()
                );
            }
        }

    }
}

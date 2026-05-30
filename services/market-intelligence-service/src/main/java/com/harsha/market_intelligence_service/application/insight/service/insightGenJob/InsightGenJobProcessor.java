package com.harsha.market_intelligence_service.application.insight.service.insightGenJob;

import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class InsightGenJobProcessor {
    private final InsightGenJobRepository insightGenJobRepository;
    private final InsightGenJobService insightGenJobService;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobProcessor.class);

    public InsightGenJobProcessor(
            InsightGenJobRepository insightGenJobRepository,
            InsightGenJobService insightGenJobService
    ) {
        this.insightGenJobRepository = insightGenJobRepository;
        this.insightGenJobService = insightGenJobService;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            List<InsightGenerationJob> jobs = insightGenJobRepository.lockNextBatch();

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

        } finally {
            processing.set(false);
        }

    }
}

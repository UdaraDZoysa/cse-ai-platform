package com.harsha.market_intelligence_service.messaging.publisher.InsightGeneration;

import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Component
public class InsightGenJobProcessor {
    private final InsightGenJobRepository insightGenJobRepository;
    private final InsightGenJobService insightGenJobService;
    private final TaskScheduler taskScheduler;
    private volatile boolean running;
    private ScheduledFuture<?> future;
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobProcessor.class);

    public InsightGenJobProcessor(
            InsightGenJobRepository insightGenJobRepository,
            InsightGenJobService insightGenJobService,
            TaskScheduler taskScheduler) {
        this.insightGenJobRepository = insightGenJobRepository;
        this.insightGenJobService = insightGenJobService;
        this.taskScheduler = taskScheduler;
    }

    public void startInsightGenJob() {
        if (running) {
            return;
        }

        running = true;
        future = taskScheduler.scheduleAtFixedRate(
                this::process,
                Duration.ofSeconds(60)
        );
    }

    public void stopInsightGenJob() {
        if (future != null) {
            future.cancel(false);
        }
        running = false;
    }

    @Transactional
    public void process() {
        List<InsightGenerationJob> jobs = insightGenJobRepository.lockNextBatch();

        if (jobs.isEmpty()) {
            stopInsightGenJob();
            return;
        }

        for (InsightGenerationJob job : jobs) {
            try {
                insightGenJobService.processJob(job);
            } catch (Exception e) {
                log.error("Unexpected failure for event → id={}, reason={}",
                        job.getId(),
                        e.getMessage());
            }
        }
    }
}

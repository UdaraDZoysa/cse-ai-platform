package com.harsha.analysis_service.application.scheduler;

import com.harsha.analysis_service.domain.repository.StockTickRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class StockTickCleanupJob {
    private final StockTickRepository repository;
    private static final Logger log = LoggerFactory.getLogger(StockTickCleanupJob.class);

    public StockTickCleanupJob(
            StockTickRepository repository
    ) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanup() {
        long cutoff = Instant
                .now()
                .minus(
                        30,
                        ChronoUnit.DAYS
                )
                        .toEpochMilli();

        int deleted = repository.deleteOlderThan(cutoff);

        log.info(
                "Deleted {} old market ticks",
                deleted
        );
    }
}

package com.harsha.market_intelligence_service.ingestion.scheduler;

import com.harsha.market_intelligence_service.ingestion.service.ApprovedAnnouncementIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IngestionScheduler {
    private final ApprovedAnnouncementIngestionService service;
    private static final Logger log = LoggerFactory.getLogger(IngestionScheduler.class);

    public IngestionScheduler(
            ApprovedAnnouncementIngestionService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 300000)
    public void ingestApprovedAnnouncements() {
        log.info("Starting approved announcement ingestion");

        service.ingest();

        log.info("Completed approved announcement ingestion");
    }
}

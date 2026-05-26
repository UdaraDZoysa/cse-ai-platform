package com.harsha.market_intelligence_service.orchestration.service;

import com.harsha.market_intelligence_service.ingestion.service.ApprovedAnnouncementIngestionService;
import com.harsha.market_intelligence_service.orchestration.model.WatchlistSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WatchlistOrchestrator {
    private final ApprovedAnnouncementIngestionService ingestionService;
    private static final Logger log = LoggerFactory.getLogger(WatchlistOrchestrator.class);

    public WatchlistOrchestrator(
            ApprovedAnnouncementIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    public void handleWatchlistUpdate(WatchlistSnapshot snapshot) {
        if (snapshot.added().isEmpty()) {
            log.info("No newly added symbols detected");
            return;
        }

        log.info(
                "Triggering targeted ingestion for: {}",
                snapshot.added()
        );

        ingestionService.ingest(snapshot.added());
    }
}

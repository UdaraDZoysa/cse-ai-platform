package com.harsha.market_intelligence_service.ingestion.scheduler;

import com.harsha.market_intelligence_service.filtering.service.TrackedSymbolService;
import com.harsha.market_intelligence_service.ingestion.service.ApprovedAnnouncementIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SafetyPollingScheduler {
    private final ApprovedAnnouncementIngestionService ingestionService;
    private final TrackedSymbolService trackedSymbolService;
    private static final Logger log = LoggerFactory.getLogger(SafetyPollingScheduler.class);

    public SafetyPollingScheduler(
            ApprovedAnnouncementIngestionService ingestionService,
            TrackedSymbolService trackedSymbolService) {
        this.ingestionService = ingestionService;
        this.trackedSymbolService = trackedSymbolService;
    }

    @Scheduled(
            fixedDelayString = "PT6H",
             initialDelayString = "PT6H")
    public void poll() {
        if (!trackedSymbolService.isInitialized()) {
            return;
        }

        log.info("Running safety polling ingestion");

        ingestionService.ingest(
                trackedSymbolService.getTrackedSymbols()
        );
    }
}

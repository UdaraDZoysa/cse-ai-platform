package com.harsha.market_intelligence_service.ingestion.scheduler;

import com.harsha.market_intelligence_service.filtering.service.TrackedSymbolService;
import com.harsha.market_intelligence_service.ingestion.announcement.service.ApprovedAnnouncementIngestionService;
import com.harsha.market_intelligence_service.ingestion.financial.service.FinancialAnnouncementIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SafetyPollingScheduler {
    private final ApprovedAnnouncementIngestionService appAnnouncementIngestionService;
    private final FinancialAnnouncementIngestionService finAnnouncementIngestionService;
    private final TrackedSymbolService trackedSymbolService;
    private static final Logger log = LoggerFactory.getLogger(SafetyPollingScheduler.class);

    public SafetyPollingScheduler(
            ApprovedAnnouncementIngestionService appAnnouncementIngestionService,
            FinancialAnnouncementIngestionService finAnnouncementIngestionService,
            TrackedSymbolService trackedSymbolService) {
        this.appAnnouncementIngestionService = appAnnouncementIngestionService;
        this.finAnnouncementIngestionService = finAnnouncementIngestionService;
        this.trackedSymbolService = trackedSymbolService;
    }

    @Scheduled(
            fixedDelayString = "PT6H",
             initialDelayString = "PT6H")
    public void poll() {
        if (!trackedSymbolService.isInitialized()) {
            return;
        }

        Set<String> trackedSymbols =
                trackedSymbolService.getTrackedSymbols();

        log.info("Running safety polling ingestion");

        appAnnouncementIngestionService.ingest(trackedSymbols);
        finAnnouncementIngestionService.ingest(trackedSymbols);
    }
}

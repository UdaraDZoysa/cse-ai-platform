package com.harsha.market_intelligence_service.orchestration.service;

import com.harsha.market_intelligence_service.ingestion.announcement.service.ApprovedAnnouncementIngestionService;
import com.harsha.market_intelligence_service.ingestion.financial.service.FinancialAnnouncementIngestionService;
import com.harsha.market_intelligence_service.orchestration.model.WatchlistSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WatchlistOrchestrator {
    private final ApprovedAnnouncementIngestionService appAnnouncementIngestionService;
    private final FinancialAnnouncementIngestionService finAnnouncementIngestionService;
    private static final Logger log = LoggerFactory.getLogger(WatchlistOrchestrator.class);

    public WatchlistOrchestrator(
            ApprovedAnnouncementIngestionService appAnnouncementIngestionService,
            FinancialAnnouncementIngestionService finAnnouncementIngestionService
    ) {
        this.appAnnouncementIngestionService = appAnnouncementIngestionService;
        this.finAnnouncementIngestionService = finAnnouncementIngestionService;
    }

    public void handleWatchlistUpdate(WatchlistSnapshot snapshot) {
        if (snapshot.added().isEmpty()) {
            log.info("No newly added symbols detected");
            return;
        }

        Set<String> addedSymbols = snapshot.added();

        log.info(
                "Triggering targeted ingestion for: {}",
                addedSymbols
        );

        appAnnouncementIngestionService.ingest(addedSymbols);
        finAnnouncementIngestionService.ingest(addedSymbols);
        System.out.println("Fin Called");
    }
}

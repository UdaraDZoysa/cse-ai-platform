package com.harsha.market_intelligence_service.orchestration.service;

import com.harsha.market_intelligence_service.ingestion.announcement.service.ApprovedAnnouncementIngestionService;
import com.harsha.market_intelligence_service.ingestion.financial.service.FinancialAnnouncementIngestionService;
import com.harsha.market_intelligence_service.narrative.service.NarrativeIntelligenceService;
import com.harsha.market_intelligence_service.orchestration.model.WatchlistSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WatchlistOrchestrator {
    private final ApprovedAnnouncementIngestionService appAnnouncementIngestionService;
    private final FinancialAnnouncementIngestionService finAnnouncementIngestionService;
    private final NarrativeIntelligenceService narrativeService;
    private static final Logger log = LoggerFactory.getLogger(WatchlistOrchestrator.class);

    public WatchlistOrchestrator(
            ApprovedAnnouncementIngestionService appAnnouncementIngestionService,
            FinancialAnnouncementIngestionService finAnnouncementIngestionService,
            NarrativeIntelligenceService narrativeService
    ) {
        this.appAnnouncementIngestionService = appAnnouncementIngestionService;
        this.finAnnouncementIngestionService = finAnnouncementIngestionService;
        this.narrativeService = narrativeService;
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
        //CSE ingestion
        appAnnouncementIngestionService.ingest(addedSymbols);
        finAnnouncementIngestionService.ingest(addedSymbols);

        // AI narrative enrichment
        for (String symbol : addedSymbols) {
            try {
                narrativeService.updateNarrativeIntelligence(symbol);

            } catch (Exception ex) {
                log.error(
                        "Narrative refresh failed for symbol={}",
                        symbol,
                        ex
                );
            }
        }
    }
}

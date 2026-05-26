package com.harsha.market_intelligence_service.narrative.schedular;

import com.harsha.market_intelligence_service.filtering.service.TrackedSymbolService;
import com.harsha.market_intelligence_service.narrative.service.NarrativeIntelligenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NarrativeRefreshScheduler {
    private final TrackedSymbolService trackedSymbolService;
    private final NarrativeIntelligenceService narrativeService;
    private static final Logger log = LoggerFactory.getLogger(NarrativeRefreshScheduler.class);

    public NarrativeRefreshScheduler(
            TrackedSymbolService trackedSymbolService,
            NarrativeIntelligenceService narrativeService) {
        this.trackedSymbolService = trackedSymbolService;
        this.narrativeService = narrativeService;
    }

    @Scheduled(
            fixedDelay = 21600000,
            initialDelay = 21600000
    )
    public void refreshNarrative() {
        for (String symbol : trackedSymbolService.getTrackedSymbols()) {
            narrativeService.refreshIfNeeded(symbol);
        }
    }
}

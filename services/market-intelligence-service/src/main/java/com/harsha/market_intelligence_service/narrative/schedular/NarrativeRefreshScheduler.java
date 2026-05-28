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
            fixedDelayString = "PT24H",
            initialDelayString = "PT24H"
    )
    public void refreshNarrative() {
        for (String symbol : trackedSymbolService.getTrackedSymbols()) {
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

package com.harsha.market_intelligence_service.filtering.service;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import com.harsha.market_intelligence_service.orchestration.model.WatchlistSnapshot;
import com.harsha.market_intelligence_service.orchestration.service.WatchlistDiffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TrackedSymbolService {
    private volatile Set<String> trackedSymbols = Set.of();
    private final WatchlistDiffService diffService;
    private static final Logger log = LoggerFactory.getLogger(TrackedSymbolService.class);

    public TrackedSymbolService(
            WatchlistDiffService diffService) {
        this.diffService = diffService;
    }

    public Set<String> getTrackedSymbols() {
        return trackedSymbols;
    }

    public boolean isInitialized() {
        return !trackedSymbols.isEmpty();
    }

    public WatchlistSnapshot handleWatchlistUpdate(WatchlistUpdatedEvent event) {
        Set<String> previous =
                trackedSymbols;

        Set<String> current =
                Set.copyOf(event.symbols());

        trackedSymbols = current;

        WatchlistSnapshot snapshot = diffService.calculate(
                previous,
                current
        );

        log.info(
                """
                Watchlist updated.
                Added symbols={}
                Removed symbols={}
                """,
                snapshot.added(),
                snapshot.removed()
        );

        return snapshot;
    }
}

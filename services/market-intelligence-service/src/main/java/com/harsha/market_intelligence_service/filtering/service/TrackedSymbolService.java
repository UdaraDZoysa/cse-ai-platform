package com.harsha.market_intelligence_service.filtering.service;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TrackedSymbolService {
    private volatile Set<String> trackedSymbols = Set.of();
    private static final Logger log = LoggerFactory.getLogger(TrackedSymbolService.class);

    public Set<String> getTrackedSymbols() {
        return trackedSymbols;
    }

    public void handleWatchlistUpdate(WatchlistUpdatedEvent event) {
        trackedSymbols = Set.copyOf(event.symbols());
        log.info(
                "Updated tracked symbols: {}",
                trackedSymbols
        );
    }
}

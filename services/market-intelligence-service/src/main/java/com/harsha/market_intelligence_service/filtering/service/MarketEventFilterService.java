package com.harsha.market_intelligence_service.filtering.service;

import org.springframework.stereotype.Service;

@Service
public class MarketEventFilterService {
    private final TrackedSymbolService trackedSymbolService;

    public MarketEventFilterService(
            TrackedSymbolService trackedSymbolService
    ) {
        this.trackedSymbolService = trackedSymbolService;
    }

    public boolean isRelevant(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            return false;
        }

        return trackedSymbolService
                .getTrackedSymbols()
                .contains(symbol);
    }
}

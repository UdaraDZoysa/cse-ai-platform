package com.harsha.market_intelligence_service.orchestration.model;

import java.util.Set;

public record WatchlistSnapshot(
        Set<String> previous,
        Set<String> current,
        Set<String> added,
        Set<String> removed
) {
}

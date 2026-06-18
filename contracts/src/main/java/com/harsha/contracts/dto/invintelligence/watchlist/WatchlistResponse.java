package com.harsha.contracts.dto.invintelligence.watchlist;

import java.time.Instant;
import java.util.Set;

public record WatchlistResponse(
        String id,
        Set<String> symbols,
        Instant updatedAt
) {
}

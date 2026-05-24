package com.harsha.contracts.events.common;

import java.time.Instant;
import java.util.Set;

public record WatchlistUpdatedEvent(
        String watchlistId,
        Set<String> symbols,
        Instant updatedAt
) {
}

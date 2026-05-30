package com.harsha.market_intelligence_service.messaging.dlt;

import java.time.Instant;

public record DltRetryScheduled(
        Instant nextAttemptAt
) {
}

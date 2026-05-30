package com.harsha.market_intelligence_service.messaging.outbox;

import java.time.Instant;

public record OutboxRetryScheduled(
        Instant nextAttemptAt
) {
}

package com.harsha.investment_intelligence_service.messaging.outbox;

import java.time.Instant;

public record OutboxRetryScheduled(
        Instant nextAttemptAt
) {
}

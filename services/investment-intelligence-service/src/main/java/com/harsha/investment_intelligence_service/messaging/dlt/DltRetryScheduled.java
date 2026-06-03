package com.harsha.investment_intelligence_service.messaging.dlt;

import java.time.Instant;

public record DltRetryScheduled(
        Instant nextAttemptAt
) {
}

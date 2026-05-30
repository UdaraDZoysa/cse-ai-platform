package com.harsha.market_intelligence_service.domain.insight.model;

import java.time.Instant;

public record InsightGenJobRetryScheduled(
        Instant nextAttemptAt
) {
}

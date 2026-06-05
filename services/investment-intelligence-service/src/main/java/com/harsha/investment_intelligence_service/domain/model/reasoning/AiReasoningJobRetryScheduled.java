package com.harsha.investment_intelligence_service.domain.model.reasoning;

import java.time.Instant;

public record AiReasoningJobRetryScheduled(
        Instant nextAttemptAt
) {
}

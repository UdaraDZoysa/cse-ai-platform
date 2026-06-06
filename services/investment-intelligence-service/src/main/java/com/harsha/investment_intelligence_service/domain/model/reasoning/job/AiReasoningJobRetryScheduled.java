package com.harsha.investment_intelligence_service.domain.model.reasoning.job;

import java.time.Instant;

public record AiReasoningJobRetryScheduled(
        Instant nextAttemptAt
) {
}

package com.harsha.investment_intelligence_service.application.api.dto;

import java.time.Instant;

public record InvInsightSummaryResponse(
        String id,
        String symbol,
        String companyName,
        String action,
        int opportunityScore,
        String riskLevel,
        Instant createdAt
) {
}

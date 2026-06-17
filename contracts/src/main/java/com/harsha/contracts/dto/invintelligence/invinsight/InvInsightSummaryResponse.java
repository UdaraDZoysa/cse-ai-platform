package com.harsha.contracts.dto.invintelligence.invinsight;

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

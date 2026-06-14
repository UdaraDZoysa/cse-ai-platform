package com.harsha.investment_intelligence_service.application.api.dto.invinsight;

import java.time.Instant;
import java.util.List;

public record InvestmentInsightDetailResponse(
        String id,
        String symbol,
        String companyName,
        String action,
        int opportunityScore,
        String riskLevel,
        String executiveSummary,
        String marketReasoning,
        String actionReasoning,
        String confidenceReasoning,
        List<String> supportingFactors,
        List<String> risks,
        List<String> invalidationConditions,
        Instant createdAt
) {
}

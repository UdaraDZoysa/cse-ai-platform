package com.harsha.investment_intelligence_service.domain.model.reasoning;

public record InvestmentReviewResult(
        String assessment,
        String recommendation,
        String risks,
        String supportingFactors,
        double confidence
) {
}

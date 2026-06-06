package com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto;

import java.util.List;

public record InvestmentReviewRaw(
        String executiveSummary,
        MarketAssessmentRaw marketAssessment,
        RecommendedActionRaw recommendedAction,
        String timeHorizon,
        ExpectedMarketBehaviorRaw expectedMarketBehavior,
        List<String> supportingFactors,
        List<String> risks,
        List<String> contextLimitations,
        List<String> invalidationConditions,
        RiskLevelRaw riskLevel,
        ConfidenceRaw confidence
) {
}

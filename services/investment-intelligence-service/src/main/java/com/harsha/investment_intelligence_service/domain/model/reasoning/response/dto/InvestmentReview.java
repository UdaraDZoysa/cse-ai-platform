package com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto;

import com.harsha.investment_intelligence_service.domain.model.reasoning.response.enums.*;

import java.util.List;

public record InvestmentReview(
        String executiveSummary,

        MarketSentiment sentiment,
        String marketReasoning,

        RecommendedAction action,
        String actionReasoning,

        TimeHorizon timeHorizon,

        ExpectedDirection expectedDirection,
        ExpectedMagnitude expectedMagnitude,
        String marketBehaviorJustification,

        List<String> supportingFactors,
        List<String> risks,
        List<String> contextLimitations,
        List<String> invalidationConditions,

        RiskLevel riskLevel,
        String riskJustification,

        int confidenceScore,
        String confidenceReasoning
) {
}

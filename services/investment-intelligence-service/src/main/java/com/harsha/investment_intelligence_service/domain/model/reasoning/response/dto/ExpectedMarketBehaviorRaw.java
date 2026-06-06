package com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto;

public record ExpectedMarketBehaviorRaw(
        String direction,
        String magnitude,
        String justification
) {
}

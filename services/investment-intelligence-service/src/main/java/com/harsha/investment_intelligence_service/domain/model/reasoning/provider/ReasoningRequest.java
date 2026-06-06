package com.harsha.investment_intelligence_service.domain.model.reasoning.provider;

public record ReasoningRequest(
        String symbol,
        String prompt,
        String model
) {
}

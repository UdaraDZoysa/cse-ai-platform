package com.harsha.investment_intelligence_service.domain.model.reasoning;

public record ReasoningRequest(
        String symbol,
        String prompt
) {
}

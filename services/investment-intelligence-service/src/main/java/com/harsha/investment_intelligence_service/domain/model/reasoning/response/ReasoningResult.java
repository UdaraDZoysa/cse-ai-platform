package com.harsha.investment_intelligence_service.domain.model.reasoning.response;

import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;

public record ReasoningResult(
        String rawResponse,
        InvestmentReview review
) {
}

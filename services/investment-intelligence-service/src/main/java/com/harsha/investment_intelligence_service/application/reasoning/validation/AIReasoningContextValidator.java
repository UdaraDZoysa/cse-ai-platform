package com.harsha.investment_intelligence_service.application.reasoning.validation;

import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningContext;
import org.springframework.stereotype.Component;

@Component
public class AIReasoningContextValidator {
    public boolean isValid(
            AIReasoningContext context
    ) {
        return context.marketSnapshot() != null
                && context.strategySummary() != null;
    }
}

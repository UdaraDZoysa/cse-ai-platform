package com.harsha.investment_intelligence_service.application.reasoning.validation;

import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import com.harsha.investment_intelligence_service.exception.ai.ResponseValidationException;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidationService {

    public void validate(
            InvestmentReview review
    ) {

        if (review == null) {
            throw new ResponseValidationException(
                    "Review cannot be null"
            );
        }

        requireText(
                review.executiveSummary(),
                "executiveSummary"
        );

        requireText(
                review.marketReasoning(),
                "marketReasoning"
        );

        requireText(
                review.actionReasoning(),
                "actionReasoning"
        );

        requireText(
                review.marketBehaviorJustification(),
                "marketBehaviorJustification"
        );

        requireText(
                review.riskJustification(),
                "riskJustification"
        );

        requireText(
                review.confidenceReasoning(),
                "confidenceReasoning"
        );

        validateConfidence(
                review.confidenceScore()
        );
    }

    private void requireText(
            String value,
            String field
    ) {

        if (value == null || value.isBlank()) {
            throw new ResponseValidationException(
                    field + " is required"
            );
        }
    }

    private void validateConfidence(
            int confidence
    ) {

        if (confidence < 0 || confidence > 100) {
            throw new ResponseValidationException(
                    "confidenceScore must be between 0 and 100"
            );
        }
    }
}

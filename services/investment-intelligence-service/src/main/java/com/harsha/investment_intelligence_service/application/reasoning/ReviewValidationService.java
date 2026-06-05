package com.harsha.investment_intelligence_service.application.reasoning;

import com.harsha.investment_intelligence_service.domain.model.reasoning.InvestmentReviewResult;
import com.harsha.investment_intelligence_service.exception.ai.ResponseValidationException;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidationService {

    public void validate(
            InvestmentReviewResult result
    ) {

        if (result == null) {
            throw new ResponseValidationException(
                    "Empty review result"
            );
        }

        if (result.assessment() == null
                || result.assessment().isBlank()) {

            throw new ResponseValidationException(
                    "Missing assessment"
            );
        }

        if (result.recommendation() == null
                || result.recommendation().isBlank()) {

            throw new ResponseValidationException(
                    "Missing recommendation"
            );
        }

        validateConfidence(
                result.confidence()
        );
    }

    private void validateConfidence(
            double confidence
    ) {

        if (Double.isNaN(confidence)
                || confidence < 0
                || confidence > 100) {

            throw new ResponseValidationException(
                    "Invalid confidence"
            );
        }
    }
}

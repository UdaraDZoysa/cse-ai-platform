package com.harsha.market_intelligence_service.domain.insight.exception;

import com.harsha.market_intelligence_service.domain.insight.model.AiProcessErrorType;

public class InsightValidationException extends NonRetryableAiException {

    public InsightValidationException(String message
    ) {
        super(message, AiProcessErrorType.VALIDATION_FAILED, null);
    }
}
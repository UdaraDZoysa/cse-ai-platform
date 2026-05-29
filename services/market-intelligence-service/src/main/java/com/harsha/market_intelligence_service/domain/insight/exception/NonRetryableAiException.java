package com.harsha.market_intelligence_service.domain.insight.exception;

import com.harsha.market_intelligence_service.domain.insight.model.AiProcessErrorType;

public class NonRetryableAiException extends AiProcessingException {

    public NonRetryableAiException(
            String message,
            AiProcessErrorType errorType,
            Throwable cause
    ) {
        super(message,errorType, cause);
    }
}

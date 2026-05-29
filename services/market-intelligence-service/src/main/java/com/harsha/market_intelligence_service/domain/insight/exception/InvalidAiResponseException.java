package com.harsha.market_intelligence_service.domain.insight.exception;

import com.harsha.market_intelligence_service.domain.insight.model.AiProcessErrorType;

public class InvalidAiResponseException extends RetryableAiException {

    public InvalidAiResponseException(
            String message,
            AiProcessErrorType errorType,
            Throwable cause
    ) {
        super(message,errorType, cause);
    }
}
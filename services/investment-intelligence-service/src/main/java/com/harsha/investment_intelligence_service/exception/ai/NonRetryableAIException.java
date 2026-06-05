package com.harsha.investment_intelligence_service.exception.ai;

import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;

public class NonRetryableAIException extends AIProcessingException{
    public NonRetryableAIException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message, errorType, cause);
    }
}

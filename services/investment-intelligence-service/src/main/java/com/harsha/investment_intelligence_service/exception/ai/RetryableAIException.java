package com.harsha.investment_intelligence_service.exception.ai;

import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;

public class RetryableAIException extends AIProcessingException{
    public RetryableAIException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message, errorType, cause);
    }
}

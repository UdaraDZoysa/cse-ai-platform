package com.harsha.investment_intelligence_service.exception.ai;

import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;

public abstract class AIProcessingException extends RuntimeException{
    private final ProcessingErrorType errorType;

    protected AIProcessingException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ProcessingErrorType getErrorType() {
        return errorType;
    }
}

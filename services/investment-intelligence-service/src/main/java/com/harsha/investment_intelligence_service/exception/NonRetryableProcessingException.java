package com.harsha.investment_intelligence_service.exception;

public class NonRetryableProcessingException extends ProcessingException {
    public NonRetryableProcessingException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message, errorType, cause);
    }
}

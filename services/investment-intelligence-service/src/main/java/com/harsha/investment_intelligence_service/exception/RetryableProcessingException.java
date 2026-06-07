package com.harsha.investment_intelligence_service.exception;

public class RetryableProcessingException extends ProcessingException {

    public RetryableProcessingException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message,errorType, cause);
    }
}

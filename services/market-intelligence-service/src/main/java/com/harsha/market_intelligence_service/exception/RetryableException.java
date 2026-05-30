package com.harsha.market_intelligence_service.exception;

public class RetryableException extends ProcessingException {

    public RetryableException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message,errorType, cause);
    }
}
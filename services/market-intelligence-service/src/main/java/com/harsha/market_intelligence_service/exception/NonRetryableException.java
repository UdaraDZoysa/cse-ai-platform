package com.harsha.market_intelligence_service.exception;

public class NonRetryableException extends ProcessingException {

    public NonRetryableException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message,errorType, cause);
    }
}

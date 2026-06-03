package com.harsha.investment_intelligence_service.exception;

public class RetryableProcessingException extends RuntimeException {

    public RetryableProcessingException(
            String message
    ) {
        super(message);
    }

    public RetryableProcessingException(
            Throwable cause
    ) {
        super(cause);
    }

    public RetryableProcessingException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}

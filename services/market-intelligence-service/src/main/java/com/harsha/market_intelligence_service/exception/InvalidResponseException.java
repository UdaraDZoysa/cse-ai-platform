package com.harsha.market_intelligence_service.exception;

public class InvalidResponseException extends RetryableException {

    public InvalidResponseException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message,errorType, cause);
    }
}
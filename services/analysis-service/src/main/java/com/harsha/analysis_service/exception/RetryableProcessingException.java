package com.harsha.analysis_service.exception;

public class RetryableProcessingException extends RuntimeException {
    public RetryableProcessingException(Throwable cause) {
        super(cause);
    }
}

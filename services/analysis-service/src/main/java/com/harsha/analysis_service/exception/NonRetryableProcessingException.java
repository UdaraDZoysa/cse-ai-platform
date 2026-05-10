package com.harsha.analysis_service.exception;

public class NonRetryableProcessingException extends RuntimeException {
    public NonRetryableProcessingException(Throwable cause) {
        super(cause);
    }
}

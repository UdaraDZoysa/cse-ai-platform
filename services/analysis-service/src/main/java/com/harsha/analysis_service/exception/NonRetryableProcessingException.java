package com.harsha.analysis_service.exception;

public class NonRetryableProcessingException extends RuntimeException {
    public NonRetryableProcessingException(
            String message
    ) {
        super(message);
    }

    public NonRetryableProcessingException(
            Throwable cause
    ) {
        super(cause);
    }

    public NonRetryableProcessingException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}

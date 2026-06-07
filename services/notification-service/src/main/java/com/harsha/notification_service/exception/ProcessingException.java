package com.harsha.notification_service.exception;

import lombok.Getter;

@Getter
public abstract class ProcessingException extends RuntimeException {

    private final ProcessingErrorType errorType;

    protected ProcessingException(
            String message,
            ProcessingErrorType errorType,
            Throwable cause
    ) {
        super(message, cause);
        this.errorType = errorType;
    }

}

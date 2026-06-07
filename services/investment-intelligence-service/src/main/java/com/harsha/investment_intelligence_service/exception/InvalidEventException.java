package com.harsha.investment_intelligence_service.exception;

public class InvalidEventException extends ProcessingException {

    public InvalidEventException(
            String message,
            Throwable cause
    ) {
        super(message, ProcessingErrorType.INVALID_EVENT, cause);
    }
}

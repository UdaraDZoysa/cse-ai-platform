package com.harsha.strategy_service.exception;

public class InvalidEventException extends RuntimeException {

    public InvalidEventException(String message) {
        super(message);
    }

    public InvalidEventException(
            Throwable cause
    ) {
        super(cause);
    }

    public InvalidEventException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}

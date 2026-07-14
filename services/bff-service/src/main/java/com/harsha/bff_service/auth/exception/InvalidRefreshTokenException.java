package com.harsha.bff_service.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(
            String message
    ) {
        super(message);
    }
}

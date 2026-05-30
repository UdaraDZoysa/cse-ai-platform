package com.harsha.market_intelligence_service.exception;

public class InsightValidationException extends NonRetryableException {

    public InsightValidationException(String message
    ) {
        super(message, ProcessingErrorType.VALIDATION_FAILED, null);
    }
}
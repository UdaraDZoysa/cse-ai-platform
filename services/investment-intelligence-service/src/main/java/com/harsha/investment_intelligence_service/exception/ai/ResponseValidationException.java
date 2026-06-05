package com.harsha.investment_intelligence_service.exception.ai;

import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;

public class ResponseValidationException extends AIProcessingException{
    public ResponseValidationException(
            String message
    ) {
        super(
                message,
                ProcessingErrorType.VALIDATION_FAILED,
                null
        );
    }
}

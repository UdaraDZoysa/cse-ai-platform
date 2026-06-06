package com.harsha.investment_intelligence_service.exception.ai;

import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;

public class AIResponseParseException
        extends NonRetryableAIException {

    public AIResponseParseException(
            String message,
            Throwable cause
    ) {
        super(message, ProcessingErrorType.INVALID_RESPONSE, cause);
    }
}

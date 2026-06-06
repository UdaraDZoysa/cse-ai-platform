package com.harsha.investment_intelligence_service.exception.ai;

import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;

public class ProviderNotFoundException extends NonRetryableAIException{

    public ProviderNotFoundException(
            String message
    ) {
        super(message, ProcessingErrorType.PROVIDER_NOT_FOUND, null);
    }
}

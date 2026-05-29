package com.harsha.market_intelligence_service.domain.insight.exception;

import com.harsha.market_intelligence_service.domain.insight.model.AiProcessErrorType;
import lombok.Getter;

@Getter
public abstract class AiProcessingException extends RuntimeException {

    private final AiProcessErrorType errorType;

    protected AiProcessingException(
            String message,
            AiProcessErrorType errorType,
            Throwable cause
    ) {
        super(message, cause);
        this.errorType = errorType;
    }

}
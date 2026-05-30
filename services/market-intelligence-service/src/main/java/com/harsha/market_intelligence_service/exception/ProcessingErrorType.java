package com.harsha.market_intelligence_service.exception;

public enum ProcessingErrorType {
    RATE_LIMIT,
    PROVIDER_UNAVAILABLE,
    NETWORK_ERROR,
    INVALID_RESPONSE,
    VALIDATION_FAILED,
    NON_RETRYABLE,
    RETRY_EXHAUSTED,
    DATABASE_ERROR,
    UNKNOWN,
    INVALID_EVENT
}

package com.harsha.investment_intelligence_service.exception;

public enum ProcessingErrorType {
    INVALID_EVENT,
    RETRY_EXHAUSTED,
    NON_RETRYABLE,
    RATE_LIMIT,
    PROVIDER_UNAVAILABLE,
    NETWORK_ERROR,
    INVALID_RESPONSE,
    VALIDATION_FAILED,
    DATABASE_ERROR,
    UNKNOWN
}

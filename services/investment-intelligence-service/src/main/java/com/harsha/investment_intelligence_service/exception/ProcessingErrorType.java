package com.harsha.investment_intelligence_service.exception;

public enum ProcessingErrorType {
    INVALID_EVENT,
    RETRY_EXHAUSTED,
    NON_RETRYABLE,
    RATE_LIMIT,
    PROVIDER_UNAVAILABLE,
    PROVIDER_NOT_FOUND,
    NETWORK_ERROR,
    INVALID_RESPONSE,
    VALIDATION_FAILED,
    DATABASE_ERROR,
    INVALID_CONFIGURATION,
    UNKNOWN
}

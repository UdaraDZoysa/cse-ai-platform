package com.harsha.market_intelligence_service.domain.insight.model;

public enum AiProcessErrorType {
    RATE_LIMIT,
    PROVIDER_UNAVAILABLE,
    NETWORK_ERROR,
    INVALID_RESPONSE,
    VALIDATION_FAILED,
    NON_RETRYABLE,
    RETRY_EXHAUSTED,
    UNKNOWN
}

package com.harsha.market_intelligence_service.domain.insight.model;

public enum InsightJobStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    SKIPPED,
    RETRY_SCHEDULED,
    FAILED
}

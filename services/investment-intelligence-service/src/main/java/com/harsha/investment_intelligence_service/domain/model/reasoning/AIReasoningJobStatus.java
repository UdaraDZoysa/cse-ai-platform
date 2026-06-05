package com.harsha.investment_intelligence_service.domain.model.reasoning;

public enum AIReasoningJobStatus {
    PENDING,
    PROCESSING,
    RETRY_SCHEDULED,
    PROCESSED,
    FAILED,
    DLT_QUEUED
}

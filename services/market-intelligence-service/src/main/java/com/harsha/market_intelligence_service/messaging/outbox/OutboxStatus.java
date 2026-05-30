package com.harsha.market_intelligence_service.messaging.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    RETRY_SCHEDULED,
    DLT_QUEUED
}

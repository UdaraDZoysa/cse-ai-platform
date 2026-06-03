package com.harsha.investment_intelligence_service.messaging.inbox;

public enum InboxStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    RETRY_SCHEDULED,
    DLT_QUEUED
}

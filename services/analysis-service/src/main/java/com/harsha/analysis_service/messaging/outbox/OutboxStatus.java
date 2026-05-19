package com.harsha.analysis_service.messaging.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    DLT_QUEUED
}

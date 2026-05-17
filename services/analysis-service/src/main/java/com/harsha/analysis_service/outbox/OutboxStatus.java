package com.harsha.analysis_service.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    DLT_QUEUED
}

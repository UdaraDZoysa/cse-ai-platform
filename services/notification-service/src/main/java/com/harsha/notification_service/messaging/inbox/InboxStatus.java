package com.harsha.notification_service.messaging.inbox;

public enum InboxStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    RETRY_SCHEDULED,
    DLT_QUEUED
}

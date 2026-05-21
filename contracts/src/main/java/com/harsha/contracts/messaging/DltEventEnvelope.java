package com.harsha.contracts.messaging;

public record DltEventEnvelope<T>(
        String eventId,
        String aggregateId,
        EventType eventType,
        String source,
        long occurredAt,
        T payload,
        String errorType,
        String errorMessage,
        int retryCount,
        long failedAt
) {
}

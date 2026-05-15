package com.harsha.contracts.messaging;

public record DltEventEnvelope(
        String eventId,
        String aggregateId,
        String eventType,
        String source,
        long occurredAt,
        String payload,
        String errorType,
        String errorMessage,
        int retryCount,
        long failedAt
) {
}

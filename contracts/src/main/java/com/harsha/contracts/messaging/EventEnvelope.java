package com.harsha.contracts.messaging;

public record EventEnvelope<T>(
        String eventId,
        String aggregateId,
        EventType eventType,
        String eventVersion,
        String source,
        long createdAt,
        T payload
) {
}

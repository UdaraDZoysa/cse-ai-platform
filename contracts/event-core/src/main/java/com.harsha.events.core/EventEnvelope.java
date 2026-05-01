package com.harsha.events.core;

public record EventEnvelope<T>(
        String eventId,
        String aggregateId,
        String eventType,
        String source,
        long occurredAt,
        T payload
) {}
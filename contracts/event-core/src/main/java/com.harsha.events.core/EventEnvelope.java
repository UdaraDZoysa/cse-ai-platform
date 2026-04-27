package com.harsha.events.core;

import java.util.UUID;

public record EventEnvelope<T>(
        UUID eventId,
        String aggregateId,
        String eventType,
        String source,
        long occurredAt,
        T payload
) {}
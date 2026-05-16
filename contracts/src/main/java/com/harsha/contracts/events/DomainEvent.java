package com.harsha.contracts.events;

import com.harsha.contracts.messaging.EventType;

public interface DomainEvent {
    EventType eventType();
    long occurredAt();
}

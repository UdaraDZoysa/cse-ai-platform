package com.harsha.strategy_service.application.events;

import com.harsha.contracts.messaging.EventType;

public interface EventPublisher {
    void publish(
            String aggregateId,
            EventType eventType,
            Object event
    );
}

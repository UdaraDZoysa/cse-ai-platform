package com.harsha.analysis_service.application.events;

public interface EventPublisher {
    void publish(
            String aggregateId,
            String eventType,
            Object event
    );
}

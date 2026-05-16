package com.harsha.analysis_service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.analysis_service.application.events.EventPublisher;
import com.harsha.analysis_service.exception.NonRetryableProcessingException;
import com.harsha.contracts.messaging.EventType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OutboxEventPublisher implements EventPublisher {
    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(
            OutboxRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(
            String aggregateId,
            EventType eventType,
            Object event
    ) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                    UUID.randomUUID(),
                    aggregateId,
                    eventType,
                    payload
            );
            repository.save(outboxEvent);

        } catch (Exception e) {
            throw new NonRetryableProcessingException(
                    "Failed to serialize event payload. eventType="
                            + eventType, e
            );
        }
    }
}

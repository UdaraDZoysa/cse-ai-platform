package com.harsha.strategy_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.messaging.EventType;
import com.harsha.strategy_service.application.events.EventPublisher;
import com.harsha.strategy_service.exception.RetryableProcessingException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class OutboxEventPublisher implements EventPublisher {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public void publish(
            String aggregateId,
            EventType eventType,
            Object event
    ) {
        JsonNode payload = objectMapper.valueToTree(event);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .build();

        try {
            outboxRepository.save(outboxEvent);

        } catch (Exception ex) {
            throw new RetryableProcessingException(
                    "Failed to persist outbox event",
                    ex
            );
        }
    }
}

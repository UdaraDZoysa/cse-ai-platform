package com.harsha.strategy_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.events.market.StockFeatureEvent;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.messaging.KafkaTopics;
import com.harsha.strategy_service.messaging.inbox.InboxEvent;
import com.harsha.strategy_service.messaging.inbox.InboxRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StockFeatureEventConsumer {
    private final ObjectMapper objectMapper;
    private final InboxRepository inboxRepository;

    public StockFeatureEventConsumer(
            ObjectMapper objectMapper,
            InboxRepository inboxRepository
    ) {
        this.objectMapper = objectMapper;
        this.inboxRepository = inboxRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.STOCK_FEATURES_V1, groupId = "${spring.kafka.consumer.group-id}")
    public void handle(EventEnvelope<StockFeatureEvent> envelope) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(envelope.payload());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }

        InboxEvent inboxEvent = new InboxEvent(
                envelope.eventId(),
                envelope.aggregateId(),
                envelope.eventType(),
                payload
        );

        try {
            inboxRepository.save(inboxEvent);
        } catch (DataIntegrityViolationException e) {
            // duplicate --> ignore
        }
    }
}

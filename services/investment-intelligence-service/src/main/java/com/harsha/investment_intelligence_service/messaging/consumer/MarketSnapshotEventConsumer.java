package com.harsha.investment_intelligence_service.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.messaging.KafkaTopics;
import com.harsha.investment_intelligence_service.messaging.inbox.InboxEvent;
import com.harsha.investment_intelligence_service.messaging.inbox.InboxRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MarketSnapshotEventConsumer {
    private final ObjectMapper objectMapper;
    private final InboxRepository inboxRepository;

    public MarketSnapshotEventConsumer(
            ObjectMapper objectMapper,
            InboxRepository inboxRepository
    ) {
        this.objectMapper = objectMapper;
        this.inboxRepository = inboxRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.MARKET_SNAPSHOT_EVENT_V1, groupId = "${spring.kafka.consumer.group-id}")
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

package com.harsha.analysis_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.analysis_service.messaging.inbox.InboxEvent;
import com.harsha.analysis_service.messaging.inbox.InboxRepository;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.contracts.messaging.KafkaTopics;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;

@Component
public class StockTickEventConsumer {
    private final ObjectMapper objectMapper;
    private final InboxRepository inboxRepository;

    public StockTickEventConsumer(
            ObjectMapper objectMapper,
            InboxRepository inboxRepository
    ) {
        this.objectMapper = objectMapper;
        this.inboxRepository = inboxRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.MARKET_TICKS_V1, groupId = "analysis-group")
    public void handle(EventEnvelope<StockTickEvent> envelope) {
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

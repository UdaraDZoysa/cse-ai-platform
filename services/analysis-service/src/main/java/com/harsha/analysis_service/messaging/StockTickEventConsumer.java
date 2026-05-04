package com.harsha.analysis_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.analysis_service.inbox.InboxEvent;
import com.harsha.analysis_service.inbox.InboxRepository;
import com.harsha.events.core.EventEnvelope;
import com.harsha.events.market.StockTickEvent;
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
    @KafkaListener(topics = "${topic.market.ticks}", groupId = "analysis-group")
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

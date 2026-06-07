package com.harsha.notification_service.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.messaging.KafkaTopics;
import com.harsha.notification_service.messaging.inbox.InboxEvent;
import com.harsha.notification_service.messaging.inbox.InboxProcessingRequested;
import com.harsha.notification_service.messaging.inbox.InboxRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class InvestmentInsightGenEventConsumer {
    private final ObjectMapper objectMapper;
    private final InboxRepository inboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InvestmentInsightGenEventConsumer(
            ObjectMapper objectMapper,
            InboxRepository inboxRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.objectMapper = objectMapper;
        this.inboxRepository = inboxRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.INVESTMENT_INSIGHT_GENERATED_EVENT_DLT_V1, groupId = "${spring.kafka.consumer.group-id}")
    public void handle(EventEnvelope<InvestmentInsightGeneratedEvent> envelope) {
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
            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(
                            new InboxProcessingRequested()
                    )
            );

        } catch (DataIntegrityViolationException e) {
            // duplicate --> ignore
        }
    }

    private void afterCommitOrNow(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            action.run();
                        }
                    }
            );
        } else {
            action.run();
        }
    }
}

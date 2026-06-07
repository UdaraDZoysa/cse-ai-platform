package com.harsha.investment_intelligence_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.events.EventPublisher;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.exception.RetryableProcessingException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Component
public class OutboxEventPublisher implements EventPublisher {
    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public OutboxEventPublisher(
            OutboxRepository repository,
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
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
            repository.save(outboxEvent);

            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new OutboxProcessingRequested())
            );

        } catch (Exception ex) {
            throw new RetryableProcessingException(
                    "Failed to persist outbox event",
                    ProcessingErrorType.DATABASE_ERROR,
                    ex
            );
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

package com.harsha.investment_intelligence_service.messaging.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class OutboxProcessor {
    private final OutboxRepository outboxRepository;
    private final OutboxEventService outboxEventService;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    public OutboxProcessor(
            OutboxRepository outboxRepository,
            OutboxEventService outboxEventService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.outboxRepository = outboxRepository;
        this.outboxEventService = outboxEventService;
        this.eventPublisher = eventPublisher;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            while (true) {
                List<OutboxEvent> events = outboxRepository.lockNextBatch();

                if (events.isEmpty()) {
                    break;
                }

                for (OutboxEvent event : events) {
                    try {
                        outboxEventService.publishSingleEvent(event);
                    } catch (Exception e) {
                        log.error(
                                "Unexpected outbox processor failure -> id={}, reason={}",
                                event.getId(),
                                e.getMessage(),
                                e
                        );
                    }
                }
            }

        } finally {
            processing.set(false);
            if (outboxRepository.existsByPendingEvents(Instant.now()) > 0) {
                eventPublisher.publishEvent(
                        new OutboxProcessingRequested()
                );
            }
        }
    }
}

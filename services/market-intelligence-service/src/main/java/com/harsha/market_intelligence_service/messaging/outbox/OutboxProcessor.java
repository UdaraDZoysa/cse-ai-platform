package com.harsha.market_intelligence_service.messaging.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class OutboxProcessor {
    private final OutboxRepository outboxRepository;
    private final OutboxEventService outboxEventService;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    public OutboxProcessor(
            OutboxRepository outboxRepository,
            OutboxEventService outboxEventService
    ) {
        this.outboxRepository = outboxRepository;
        this.outboxEventService = outboxEventService;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            List<OutboxEvent> events = outboxRepository.lockNextBatch();

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

        } finally {
            processing.set(false);
        }
    }
}

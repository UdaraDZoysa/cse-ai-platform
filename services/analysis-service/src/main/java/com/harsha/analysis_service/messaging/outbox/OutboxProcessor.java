package com.harsha.analysis_service.messaging.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class OutboxProcessor {
    private final OutboxRepository outboxRepository;
    private final OutboxEventService outboxEventService;

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    public OutboxProcessor(
            OutboxRepository outboxRepository,
            OutboxEventService outboxEventService
    ) {
        this.outboxRepository = outboxRepository;
        this.outboxEventService = outboxEventService;
    }

    @Scheduled(fixedRate = 5000)
    public void process() {
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
    }
}

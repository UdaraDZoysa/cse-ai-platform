package com.harsha.notification_service.messaging.inbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class InboxProcessor {
    private final InboxRepository inboxRepository;
    private final InboxEventService inboxEventService;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(InboxProcessor.class);

    public InboxProcessor(
            InboxRepository inboxRepository,
            InboxEventService inboxEventService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.inboxRepository = inboxRepository;
        this.inboxEventService = inboxEventService;
        this.eventPublisher = eventPublisher;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }

        try {
            while (true) {
                List<InboxEvent> events = inboxRepository.lockNextBatch();

                if (events.isEmpty()) {
                    break;
                }

                for (InboxEvent event : events) {
                    try {
                        inboxEventService.processSingleEvent(event);
                    } catch (Exception e) {
                        log.error("Unexpected failure for event → id={}, reason={}",
                                event.getId(),
                                e.getMessage());
                    }
                }
            }

        } finally {
            processing.set(false);
            if (inboxRepository.existsByPendingEvents(Instant.now()) > 0) {
                eventPublisher.publishEvent(
                        new InboxProcessingRequested()
                );
            }
        }
    }
}

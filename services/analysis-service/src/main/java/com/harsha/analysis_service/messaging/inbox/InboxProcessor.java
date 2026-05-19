package com.harsha.analysis_service.messaging.inbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InboxProcessor {
    private final InboxRepository inboxRepository;
    private final InboxEventService inboxEventService;

    private static final Logger log = LoggerFactory.getLogger(InboxProcessor.class);

    public InboxProcessor(
            InboxRepository inboxRepository,
            InboxEventService inboxEventService)
    {
        this.inboxRepository = inboxRepository;
        this.inboxEventService = inboxEventService;
    }

    @Scheduled(fixedRate = 5000)
    public void process() {
        List<InboxEvent> events = inboxRepository.lockNextBatch();

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
}

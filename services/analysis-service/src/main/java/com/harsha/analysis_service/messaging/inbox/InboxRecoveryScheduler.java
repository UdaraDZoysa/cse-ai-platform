package com.harsha.analysis_service.messaging.inbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class InboxRecoveryScheduler {
    private final InboxRepository inboxRepository;
    private static final Logger log = LoggerFactory.getLogger(InboxRecoveryScheduler.class);

    public InboxRecoveryScheduler(
            InboxRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<InboxEvent> stuckEvents =
                inboxRepository.findStuckProcessingEvents(cutoff);

        for (InboxEvent event : stuckEvents) {
            log.warn(
                    "Recovering stuck event → id={}, processingStartedAt={}",
                    event.getId(),
                    event.getProcessingStartedAt()
            );
            event.markPending();
            inboxRepository.save(event);
        }
    }
}

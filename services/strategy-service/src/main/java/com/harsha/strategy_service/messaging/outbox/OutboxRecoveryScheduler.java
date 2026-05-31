package com.harsha.strategy_service.messaging.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxRecoveryScheduler {
    private final OutboxRepository outboxRepository;
    private static final Logger log = LoggerFactory.getLogger(OutboxRecoveryScheduler.class);

    public OutboxRecoveryScheduler(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<OutboxEvent> stuckEvents =
                outboxRepository.findStuckProcessingJobs(cutoff);

        for (OutboxEvent event : stuckEvents) {
            log.debug(
                    """
                    
                    Recovering stuck event.
                    
                    id={}
                    symbol={}
                    processingStartedAt={}
                    
                    """,
                    event.getId(),
                    event.getAggregateId(),
                    event.getUpdatedAt()
            );
            event.setNextAttemptAt(Instant.now());
            event.markRetryScheduled();
            outboxRepository.save(event);
        }
    }
}

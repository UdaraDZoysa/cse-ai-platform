package com.harsha.market_intelligence_service.messaging.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxEventRecoveryScheduler {
    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(OutboxEventRecoveryScheduler.class);

    public OutboxEventRecoveryScheduler(
            OutboxRepository outboxRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<OutboxEvent> stuckEvents =
                outboxRepository.findStuckProcessingJobs(cutoff);

        for (OutboxEvent stuckEvent : stuckEvents) {
            log.info(
                    """
                    Recovering stuck Outbox Event.
                    
                    id={}
                    symbol={}
                    processingStartedAt={}
                    
                    """,
                    stuckEvent.getId(),
                    stuckEvent.getAggregateId(),
                    stuckEvent.getUpdatedAt()
            );
            stuckEvent.setNextAttemptAt(Instant.now());
            stuckEvent.markRetryScheduled();
            outboxRepository.save(stuckEvent);
        }
        if (!stuckEvents.isEmpty()) {
            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new OutboxProcessingRequested())
            );
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void recover() {
        eventPublisher.publishEvent(
                new OutboxProcessingRequested()
        );
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

package com.harsha.market_intelligence_service.messaging.dlt;

import com.harsha.market_intelligence_service.messaging.outbox.OutboxProcessingRequested;
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
public class DltRecoveryScheduler {
    private final DltRepository dltRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(DltRecoveryScheduler.class);

    public DltRecoveryScheduler(
            DltRepository dltRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.dltRepository = dltRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<DltMessage> stuckDltMessages =
                dltRepository.findStuckProcessingJobs(cutoff);

        for (DltMessage stuckDltMessage : stuckDltMessages) {
            log.info(
                    "Recovering stuck Dlt Message → id={}, processingStartedAt={}",
                    stuckDltMessage.getId(),
                    stuckDltMessage.getUpdatedAt()
            );
            stuckDltMessage.setNextAttemptAt(Instant.now());
            stuckDltMessage.markRetryScheduled();
            dltRepository.save(stuckDltMessage);
        }
        if (!stuckDltMessages.isEmpty()) {
            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new DltProcessingRequested())
            );
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void recover() {
        eventPublisher.publishEvent(
                new DltProcessingRequested()
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

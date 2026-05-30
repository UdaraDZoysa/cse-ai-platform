package com.harsha.market_intelligence_service.application.insight.service.insightGenJob;

import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.model.InsightGenJobProcessingRequested;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
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
public class InsightGenJobRecoveryScheduler {
    private final InsightGenJobRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobRecoveryScheduler.class);

    public InsightGenJobRecoveryScheduler(
            InsightGenJobRepository repository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<InsightGenerationJob> stuckJobs =
                repository.findStuckProcessingJobs(cutoff);

        for (InsightGenerationJob stuckJob : stuckJobs) {
            log.info(
                    "Recovering stuck job → id={}, processingStartedAt={}",
                    stuckJob.getId(),
                    stuckJob.getUpdatedAt()
            );
            stuckJob.setNextAttemptAt(Instant.now());
            stuckJob.markRetryScheduled();
            stuckJob.setFailureReason(null);
            stuckJob.setErrorType(null);
            repository.save(stuckJob);
        }
        if (!stuckJobs.isEmpty()) {
            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new InsightGenJobProcessingRequested())
            );
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void recover() {
        eventPublisher.publishEvent(new InsightGenJobProcessingRequested());
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

package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.job.AiReasoningJobProcessingRequest;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
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
public class AiReasoningJobRecoveryScheduler {
    private final AiReasoningJobRepository aiReasoningJobRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(AiReasoningJobRecoveryScheduler.class);

    public AiReasoningJobRecoveryScheduler(
            AiReasoningJobRepository aiReasoningJobRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.aiReasoningJobRepository = aiReasoningJobRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<AiReasoningJob> stuckJobs =
                aiReasoningJobRepository.findStuckProcessingEvents(cutoff);

        for (AiReasoningJob stuckJob : stuckJobs) {
            log.info(
                    "Recovering stuck job → id={}, processingStartedAt={}",
                    stuckJob.getId(),
                    stuckJob.getUpdatedAt()
            );
            stuckJob.setNextAttemptAt(Instant.now());
            stuckJob.markRetryScheduled();
            stuckJob.setErrorMessage(null);
            stuckJob.setErrorType(null);
            aiReasoningJobRepository.save(stuckJob);
        }
        if (!stuckJobs.isEmpty()) {
            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new AiReasoningJobProcessingRequest())
            );
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void recover() {
        eventPublisher.publishEvent(new AiReasoningJobProcessingRequest());
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

package com.harsha.market_intelligence_service.application.insight.service.insightGenJob;

import com.harsha.market_intelligence_service.application.insight.trigger.MarketInsightExecutionService;
import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.model.InsightGenJobRetryScheduled;
import com.harsha.market_intelligence_service.exception.NonRetryableException;
import com.harsha.market_intelligence_service.exception.RetryableException;
import com.harsha.market_intelligence_service.exception.ProcessingErrorType;
import com.harsha.market_intelligence_service.domain.insight.model.InsightExecutionResult;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
import com.harsha.market_intelligence_service.messaging.outbox.OutboxRetryScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class InsightGenJobService {
    private final InsightGenJobRepository insightGenJobRepository;
    private final MarketInsightExecutionService marketInsightExecutionService;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobService.class);

    public InsightGenJobService(
            InsightGenJobRepository insightGenJobRepository,
            MarketInsightExecutionService marketInsightExecutionService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.insightGenJobRepository = insightGenJobRepository;
        this.marketInsightExecutionService = marketInsightExecutionService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processJob(InsightGenerationJob job){
        try {
            job.markProcessing();
            insightGenJobRepository.save(job);

            InsightExecutionResult result = marketInsightExecutionService.trigger(
                    job.getSymbol()
            );

            if (!result.generated()) {
                job.markSkipped();
                insightGenJobRepository.save(job);

                log.info(
                        """
                        Insight generation skipped.
    
                        symbol={}
                        reason={}
                        """,
                        job.getSymbol(),
                        result.reason()
                );

                return;
            }

            job.markProcessed();
            insightGenJobRepository.save(job);

        } catch (RetryableException ex) {
            handleRetry(job, ex.getErrorType(), ex);

        } catch (NonRetryableException ex) {
            markProcessFailed(job, ex.getErrorType(), ex);

        }
        catch (Exception ex) {
            handleRetry(job, ProcessingErrorType.UNKNOWN, ex);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(
                300000,
                Math.pow(2, retryCount) * 1000
        );

        double jitter = ThreadLocalRandom.current()
                .nextDouble(0.8, 1.2);

        return (long) (baseDelay * jitter);
    }

    public void markProcessFailed(
            InsightGenerationJob job,
            ProcessingErrorType errorType,
            Exception ex
    ) {
        job.markFailed();
        job.setFailureReason(ex.getMessage());
        job.setErrorType(errorType);
        insightGenJobRepository.save(job);

        log.error(
                """
                Insight generation failed.
    
                symbol={}
                errorType={}
                retryCount={}
                reason={}
                """,
                job.getSymbol(),
                errorType,
                job.getRetryCount(),
                ex.getMessage(),
                ex
        );
    }

    private void handleRetry(
            InsightGenerationJob job,
            ProcessingErrorType errorType,
            Exception ex
    ) {
        job.markAttempt();

        if (!job.shouldRetry()) {
            markProcessFailed(job, ProcessingErrorType.RETRY_EXHAUSTED, ex);
            return;
        }

        long backoff = calculateBackoff(job.getRetryCount());

        Instant nextAttemptAt = Instant.now().plusMillis(backoff);

        job.setNextAttemptAt(nextAttemptAt);

        job.setErrorType(errorType);

        job.setFailureReason(ex.getMessage());

        job.markRetryScheduled();

        insightGenJobRepository.save(job);

        afterCommitOrNow(() ->
                eventPublisher.publishEvent(new InsightGenJobRetryScheduled(nextAttemptAt))
        );

        log.warn(
                """
                Scheduling retry Insight generation Job.
    
                symbol={}
                retryCount={}
                nextAttemptAt={}
                reason={}
                """,
                job.getSymbol(),
                job.getRetryCount(),
                job.getNextAttemptAt(),
                ex.getMessage()
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

package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import com.harsha.investment_intelligence_service.application.reasoning.orchestrator.ReasoningOrchestrator;
import com.harsha.investment_intelligence_service.application.review.publisher.ReviewPublisher;
import com.harsha.investment_intelligence_service.application.review.publisher.ReviewPublisherRegistry;
import com.harsha.investment_intelligence_service.config.LlmProperties;
import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.job.AiReasoningJobRetryScheduled;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.ReasoningResponse;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.exception.ai.NonRetryableAIException;
import com.harsha.investment_intelligence_service.exception.ai.RetryableAIException;
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
public class AiReasoningJobService {
    private final AiReasoningJobRepository aiReasoningJobRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ReasoningOrchestrator orchestrator;
    private final ReviewPublisherRegistry reviewPublisherRegistry;
    private final ProviderRateLimitState rateLimitState;
    private final LlmProperties llmProperties;
    private static final Logger log = LoggerFactory.getLogger(AiReasoningJobService.class);

    public AiReasoningJobService(
            AiReasoningJobRepository aiReasoningJobRepository,
            ApplicationEventPublisher applicationEventPublisher,
            ReasoningOrchestrator orchestrator,
            ReviewPublisherRegistry reviewPublisherRegistry,
            ProviderRateLimitState rateLimitState,
            LlmProperties llmProperties
    ) {
        this.aiReasoningJobRepository = aiReasoningJobRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.orchestrator = orchestrator;
        this.reviewPublisherRegistry = reviewPublisherRegistry;
        this.rateLimitState = rateLimitState;
        this.llmProperties = llmProperties;
    }

    @Transactional
    public void processJob(AiReasoningJob job) {
        try {
            job.markProcessing();
            aiReasoningJobRepository.save(job);

            ReasoningResponse result =
                    orchestrator.generateResponse(
                            job.getSymbol(),
                            job.getPrompt()
                    );

            job.setRawResponse(result.rawResponse());
            job.setModel(llmProperties.model());
            job.setProviderType(llmProperties.providerType());
            job.markPartiallyProcessed();
            aiReasoningJobRepository.save(job);

            log.info(
                    """
                    AI reasoning completed.
            
                    symbol={}
                    provider={}
                    model={}
                    """,
                    job.getSymbol(),
                    job.getProviderType(),
                    job.getModel()
            );

            InvestmentReview review =
                    orchestrator.parseInvestmentReview(result.rawResponse());

            job.setParsedReview(review);
            job.markProcessed();
            aiReasoningJobRepository.save(job);

            ReviewPublisher publisher =
                    reviewPublisherRegistry.get(
                            job.getReviewType()
                    );

            publisher.publish(
                    review,
                    job
            );

        } catch (RetryableAIException ex) {
            handleRetry(job, ex.getErrorType(), ex);

        } catch (NonRetryableAIException ex) {
            markProcessFailed(job, ex.getErrorType(), ex);

        }
        catch (Exception ex) {
            handleRetry(job, ProcessingErrorType.UNKNOWN, ex);
        }
    }

    private long calculateBackoff(
            int retryCount,
            ProcessingErrorType errorType
    ) {

        if (errorType == ProcessingErrorType.RATE_LIMIT) {

            long baseDelay = Math.max(60_000, rateLimitState.remainingMillis());

            return baseDelay +
                    ThreadLocalRandom
                            .current()
                            .nextLong(0, 30_000);
        }

        long baseDelay = (long) Math.min(60_000, Math.pow(2, retryCount) * 1000);

        double jitter = 0.5 + Math.random();

        return (long) (baseDelay * jitter);
    }

    private void markProcessFailed(
            AiReasoningJob job,
            ProcessingErrorType errorType,
            Exception ex
    ) {
        job.markFailed();
        job.setErrorMessage(ex.getMessage());
        job.setErrorType(errorType);
        aiReasoningJobRepository.save(job);

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
            AiReasoningJob job,
            ProcessingErrorType errorType,
            Exception ex
    ) {

        job.markAttempt();

        if (!shouldRetry(job, errorType)) {
            markProcessFailed(
                    job,
                    ProcessingErrorType.RETRY_EXHAUSTED,
                    ex
            );
            return;
        }

        long backoff = calculateBackoff(
                job.getRetryCount(),
                errorType
        );

        Instant nextAttemptAt = Instant.now().plusMillis(backoff);

        job.setNextAttemptAt(nextAttemptAt);

        job.setErrorType(errorType);

        job.setErrorMessage(ex.getMessage());

        job.markRetryScheduled();

        aiReasoningJobRepository.save(job);

        afterCommitOrNow(() ->
                applicationEventPublisher.publishEvent(new AiReasoningJobRetryScheduled(nextAttemptAt))
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

    private boolean shouldRetry(
            AiReasoningJob job,
            ProcessingErrorType errorType
    ) {

        if (errorType == ProcessingErrorType.RATE_LIMIT) {
            return job.getRetryCount() < 5;
        }

        return job.shouldRetry();
    }
}

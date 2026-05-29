package com.harsha.market_intelligence_service.messaging.publisher.InsightGeneration;

import com.harsha.market_intelligence_service.application.insight.trigger.MarketInsightExecutionService;
import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.exception.NonRetryableAiException;
import com.harsha.market_intelligence_service.domain.insight.exception.RetryableAiException;
import com.harsha.market_intelligence_service.domain.insight.model.AiProcessErrorType;
import com.harsha.market_intelligence_service.domain.insight.model.InsightExecutionResult;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InsightGenJobService {
    private final InsightGenJobRepository insightGenJobRepository;
    private final MarketInsightExecutionService marketInsightExecutionService;
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobService.class);

    public InsightGenJobService(
            InsightGenJobRepository insightGenJobRepository,
            MarketInsightExecutionService marketInsightExecutionService) {
        this.insightGenJobRepository = insightGenJobRepository;
        this.marketInsightExecutionService = marketInsightExecutionService;
    }

    public void processJob(InsightGenerationJob job){
        try {
            job.markAttempt();
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

        } catch (RetryableAiException ex) {
            handleRetry(job, ex.getErrorType(), ex);

        } catch (NonRetryableAiException ex) {
            markProcessFailed(job, ex.getErrorType(), ex);

        }
        catch (Exception ex) {
            handleRetry(job, AiProcessErrorType.UNKNOWN, ex);
        }
    }

    private long calculateBackoff(int retryCount) {
        long baseDelay = (long) Math.min(60000, Math.pow(2, retryCount) * 1000);
        double jitter = 0.5 + Math.random();
        return (long) (baseDelay * jitter);
    }

    public void markProcessFailed(
            InsightGenerationJob job,
            AiProcessErrorType errorType,
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
            AiProcessErrorType errorType,
            Exception ex
    ) {
        job.markAttempt();

        if (!job.shouldRetry()) {
            markProcessFailed(job, AiProcessErrorType.RETRY_EXHAUSTED, ex);
            return;
        }

        long backoff = calculateBackoff(job.getRetryCount());

        job.setNextAttemptAt(Instant.now().plusMillis(backoff));

        job.setErrorType(errorType);

        job.setFailureReason(ex.getMessage());

        job.markRetryScheduled();

        insightGenJobRepository.save(job);

        log.warn(
                """
                Scheduling retry.
    
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
}

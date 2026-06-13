package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.job.AiReasoningJobProcessingRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.job.AiReasoningJobRetryScheduled;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AiReasoningJobProcessor {
    private final AiReasoningJobRepository repository;
    private final AiReasoningJobService aiReasoningJobService;
    private final ApplicationEventPublisher eventPublisher;
    private final ProviderRateLimitState rateLimitState;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(AiReasoningJobProcessor.class);

    public AiReasoningJobProcessor(
            AiReasoningJobRepository repository,
            AiReasoningJobService aiReasoningJobService,
            ApplicationEventPublisher eventPublisher, ProviderRateLimitState rateLimitState
    ) {
        this.repository = repository;
        this.aiReasoningJobService = aiReasoningJobService;
        this.eventPublisher = eventPublisher;
        this.rateLimitState = rateLimitState;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            while (true) {
                List<AiReasoningJob> jobs = repository.lockNextBatch();

                if (jobs.isEmpty()) {
                    break;
                }

                for (AiReasoningJob job : jobs) {

                    if (rateLimitState.isBlocked()) {
                        return;
                    }

                    try {
                        aiReasoningJobService.processJob(job);
                    } catch (Exception ex) {
                        log.error(
                                """
                                Unexpected job failure.
                                
                                jobId={}
                                symbol={}
                                reason={}
                                """,
                                job.getId(),
                                job.getSymbol(),
                                ex.getMessage(),
                                ex
                        );
                    }
                }
            }

        } finally {
            processing.set(false);
            if (repository.existsByPendingJob(Instant.now()) > 0) {
                if (rateLimitState.isBlocked()) {
                    eventPublisher.publishEvent(
                            new AiReasoningJobRetryScheduled(
                                    Instant.now().plusMillis(
                                            rateLimitState.remainingMillis() + 2000
                                    )
                            )
                    );

                } else {
                    eventPublisher.publishEvent(
                            new AiReasoningJobProcessingRequest()
                    );
                }
            }
        }
    }
}

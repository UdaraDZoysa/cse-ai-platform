package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AiReasoningJobProcessingRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AiReasoningJobRetryScheduled;
import com.harsha.investment_intelligence_service.domain.model.reasoning.ReviewType;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.exception.RetryableProcessingException;
import com.harsha.investment_intelligence_service.exception.ai.RetryableAIException;
import com.harsha.investment_intelligence_service.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class AiReasoningJobPersistenceService {
    private final AiReasoningJobRepository reasoningJobRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(AiReasoningJobPersistenceService.class);
    @Value("${groq.model}")
    private String model;

    public AiReasoningJobPersistenceService(
            AiReasoningJobRepository reasoningJobRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.reasoningJobRepository = reasoningJobRepository;
        this.eventPublisher = eventPublisher;
    }

    public void persistAiReasoningJob(
            String symbol,
            ReviewType reviewType,
            String prompt,
            String reasoningContext
    ) {
        String contextHash = HashUtil.sha256(reasoningContext);

        AiReasoningJob reasoningJob = AiReasoningJob.builder()
                .id(UUID.randomUUID().toString())
                .symbol(symbol)
                .model(model)
                .reviewType(reviewType)
                .prompt(prompt)
                .reasoningContext(reasoningContext)
                .contextHash(contextHash)
                .build();

        try {
            reasoningJobRepository.save(reasoningJob);

            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new AiReasoningJobProcessingRequest())
            );

            log.debug(
                    "AI Reasoning job created. symbol={}, hash={}",
                    symbol,
                    contextHash
            );

        } catch (DataIntegrityViolationException ex) {

            log.info(
                    "AI Reasoning job already exists. symbol={}, hash={}",
                    symbol,
                    contextHash
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to persist insight generation job. symbol={}",
                    symbol,
                    ex
            );

            throw new RetryableProcessingException(
                    "Failed to persist AI Reasoning job",
                    ex
            );
        }
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

package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import com.harsha.investment_intelligence_service.config.LlmProperties;
import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.job.AiReasoningJobProcessingRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.ReviewType;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.exception.RetryableProcessingException;
import com.harsha.investment_intelligence_service.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final LlmProperties llmProperties;
    private static final Logger log = LoggerFactory.getLogger(AiReasoningJobPersistenceService.class);

    public AiReasoningJobPersistenceService(
            AiReasoningJobRepository reasoningJobRepository,
            ApplicationEventPublisher eventPublisher,
            LlmProperties llmProperties
    ) {
        this.reasoningJobRepository = reasoningJobRepository;
        this.eventPublisher = eventPublisher;
        this.llmProperties = llmProperties;
    }

    public void persistAiReasoningJob(
            String symbol,
            String companyName,
            ReviewType reviewType,
            String prompt,
            String reasoningContext
    ) {
        String contextHash = HashUtil.sha256(reasoningContext);

        System.out.println("#############company:"+companyName);

        AiReasoningJob reasoningJob = AiReasoningJob.builder()
                .id(UUID.randomUUID().toString())
                .symbol(symbol)
                .companyName(companyName)
                .model(llmProperties.model())
                .providerType(llmProperties.providerType())
                .reviewType(reviewType)
                .prompt(prompt)
                .reasoningContext(reasoningContext)
                .contextHash(contextHash)
                .build();

        try {
            AiReasoningJob result = reasoningJobRepository.save(reasoningJob);

            System.out.println("#############result_Company:"+result.getCompanyName());

            afterCommitOrNow(() ->
                    eventPublisher.publishEvent(new AiReasoningJobProcessingRequest())
            );

            log.debug(
                    "AI Reasoning job created. symbol={}, hash={}",
                    symbol,
                    contextHash
            );

        } catch (DataIntegrityViolationException ex) {

            log.error(
                    """
                    Data integrity violation.
        
                    symbol={}
                    hash={}
                    reason={}
                    """,
                    symbol,
                    contextHash,
                    ex.getMostSpecificCause().getMessage(),
                    ex
            );

            throw ex;

        } catch (Exception ex) {
            log.error(
                    "Failed to persist insight generation job. symbol={}",
                    symbol,
                    ex
            );

            throw new RetryableProcessingException(
                    "Failed to persist AI Reasoning job",
                    ProcessingErrorType.DATABASE_ERROR,
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

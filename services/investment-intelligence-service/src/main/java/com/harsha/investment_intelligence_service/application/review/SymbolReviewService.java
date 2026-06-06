package com.harsha.investment_intelligence_service.application.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.investment_intelligence_service.application.context.ContextAssembler;
import com.harsha.investment_intelligence_service.application.reasoning.validation.AIReasoningContextValidator;
import com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob.AiReasoningJobPersistenceService;
import com.harsha.investment_intelligence_service.application.reasoning.prompt.PromptBuilder;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.model.reasoning.ReviewType;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.exception.ai.NonRetryableAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class SymbolReviewService {
    private final SymbolContextRepository repository;
    private final ContextAssembler assembler;
    private final AIReasoningContextValidator validator;
    private final PromptBuilder promptBuilder;
    private final ReviewPriorityCalculator priorityCalculator;
    private final AiReasoningJobPersistenceService persistenceService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(SymbolReviewService.class);

    public SymbolReviewService(
            SymbolContextRepository repository,
            ContextAssembler assembler,
            AIReasoningContextValidator validator,
            PromptBuilder promptBuilder,
            ReviewPriorityCalculator priorityCalculator,
            AiReasoningJobPersistenceService persistenceService,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.assembler = assembler;
        this.validator = validator;
        this.promptBuilder = promptBuilder;
        this.priorityCalculator = priorityCalculator;
        this.persistenceService = persistenceService;
        this.objectMapper = objectMapper;
    }

    public void reviewAllSymbols() {
        repository.findAll()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                (SymbolContext context) ->
                                        priorityCalculator
                                                .calculate(context)
                                                .score()
                        ).reversed()
                )
                .forEach(this::review);
    }

    private void review(
            SymbolContext context
    ) {
        var reasoningContext = assembler.assemble(context);

        if (!validator.isValid(reasoningContext)) {
            return;
        }

        var request = promptBuilder.build(
                reasoningContext
        );

        try {
            String strReasoningContext = objectMapper.writeValueAsString(
                    reasoningContext
            );

            System.out.println("Reviewing symbol: " + strReasoningContext);

            persistenceService.persistAiReasoningJob(
                    request.symbol(),
                    ReviewType.PERIODIC_REVIEW,
                    request.prompt(),
                    strReasoningContext
            );

        } catch (JsonProcessingException e) {
            throw new NonRetryableAIException(
                    "Failed to serialize reasoning context",
                    ProcessingErrorType.NON_RETRYABLE,
                    e
            );
        }

        log.info("""
                ======================================

                AI REVIEW REQUEST

                symbol={}

                {}

                ======================================
                """,
                request.symbol(),
                request.prompt()
        );
    }
}

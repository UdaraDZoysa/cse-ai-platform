package com.harsha.investment_intelligence_service.application.review;

import com.harsha.investment_intelligence_service.application.context.ContextAssembler;
import com.harsha.investment_intelligence_service.application.reasoning.AIReasoningContextValidator;
import com.harsha.investment_intelligence_service.application.reasoning.PromptBuilder;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SymbolReviewService {
    private final SymbolContextRepository repository;
    private final ContextAssembler assembler;
    private final AIReasoningContextValidator validator;
    private final PromptBuilder promptBuilder;
    private static final Logger log = LoggerFactory.getLogger(SymbolReviewService.class);

    public SymbolReviewService(
            SymbolContextRepository repository,
            ContextAssembler assembler,
            AIReasoningContextValidator validator,
            PromptBuilder promptBuilder
    ) {
        this.repository = repository;
        this.assembler = assembler;
        this.validator = validator;
        this.promptBuilder = promptBuilder;
    }

    public void reviewAllSymbols() {
        repository.findAll()
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

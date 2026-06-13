package com.harsha.investment_intelligence_service.application.reasoning.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.investment_intelligence_service.application.reasoning.parser.JsonResponseCleaner;
import com.harsha.investment_intelligence_service.application.reasoning.provider.ReasoningProvider;
import com.harsha.investment_intelligence_service.application.reasoning.provider.ReasoningProviderRegistry;
import com.harsha.investment_intelligence_service.application.reasoning.validation.ReviewValidationService;
import com.harsha.investment_intelligence_service.application.review.InvestmentReviewMapper;
import com.harsha.investment_intelligence_service.config.LlmProperties;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ProviderType;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ReasoningRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReviewRaw;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.ReasoningResponse;
import com.harsha.investment_intelligence_service.exception.ai.AIResponseParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReasoningOrchestrator {
    private final ReasoningProviderRegistry reasoningProviderRegistry;
    private final ObjectMapper objectMapper;
    private final JsonResponseCleaner jsonResponseCleaner;
    private final InvestmentReviewMapper investmentReviewMapper;
    private final ReviewValidationService reviewValidationService;
    private final LlmProperties llmProperties;
    private static final Logger log = LoggerFactory.getLogger(ReasoningOrchestrator.class);

    public ReasoningOrchestrator(
            ReasoningProviderRegistry reasoningProviderRegistry,
            ObjectMapper objectMapper,
            JsonResponseCleaner jsonResponseCleaner,
            InvestmentReviewMapper investmentReviewMapper,
            ReviewValidationService reviewValidationService,
            LlmProperties llmProperties
    ) {
        this.reasoningProviderRegistry = reasoningProviderRegistry;
        this.objectMapper = objectMapper;
        this.jsonResponseCleaner = jsonResponseCleaner;
        this.investmentReviewMapper = investmentReviewMapper;
        this.reviewValidationService = reviewValidationService;
        this.llmProperties = llmProperties;
    }

    public ReasoningResponse generateResponse(
            String symbol,
            String prompt
    ) {
        ReasoningProvider provider = reasoningProviderRegistry.get(
                llmProperties.providerType()
        );

        return provider.generate(
                new ReasoningRequest(
                        symbol,
                        prompt,
                        llmProperties.model()
                )
        );
    }

    public InvestmentReview parseInvestmentReview(String rawResponse) {
        try {
            String cleanedResponse = jsonResponseCleaner.clean(rawResponse);

            log.info(
                    """
                    CLEANED RESPONSE
                    
                    {}
                    """,
                    cleanedResponse
            );

            InvestmentReviewRaw reviewRaw =
                    objectMapper.readValue(
                            cleanedResponse,
                            InvestmentReviewRaw.class
                    );

            InvestmentReview review = investmentReviewMapper.map(reviewRaw);

            reviewValidationService.validate(review);

            return review;
        } catch (JsonProcessingException ex) {
            throw new AIResponseParseException(
                    "Failed to parse AI response",
                    ex
            );
        }
    }
}

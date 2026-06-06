package com.harsha.investment_intelligence_service.application.reasoning.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.investment_intelligence_service.application.reasoning.validation.ReviewValidationService;
import com.harsha.investment_intelligence_service.application.review.InvestmentReviewMapper;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReviewRaw;
import com.harsha.investment_intelligence_service.exception.ai.AIResponseParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InvestmentReviewParser {
    private final ObjectMapper objectMapper;
    private final JsonResponseCleaner jsonResponseCleaner;
    private final InvestmentReviewMapper investmentReviewMapper;
    private final ReviewValidationService reviewValidationService;
    private static final Logger log = LoggerFactory.getLogger(InvestmentReviewParser.class);



    public InvestmentReviewParser(
            ObjectMapper objectMapper,
            JsonResponseCleaner jsonResponseCleaner,
            InvestmentReviewMapper investmentReviewMapper,
            ReviewValidationService reviewValidationService
    ) {
        this.objectMapper = objectMapper;
        this.jsonResponseCleaner = jsonResponseCleaner;
        this.investmentReviewMapper = investmentReviewMapper;
        this.reviewValidationService = reviewValidationService;
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

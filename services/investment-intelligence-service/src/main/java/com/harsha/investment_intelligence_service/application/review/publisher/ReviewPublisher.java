package com.harsha.investment_intelligence_service.application.review.publisher;

import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.ReviewType;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;

public interface ReviewPublisher {
    ReviewType supportedType();

    void publish(
            InvestmentReview review,
            AiReasoningJob job
    );
}

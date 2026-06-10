package com.harsha.investment_intelligence_service.application.api.mapper;

import com.harsha.investment_intelligence_service.application.api.dto.InvInsightSummaryResponse;
import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import org.springframework.stereotype.Component;

@Component
public class InvInsightMapper {
    public InvInsightSummaryResponse toResponse(
            AiReasoningJob job
    ) {
        InvestmentReview review =
                job.getParsedReview();

        return new InvInsightSummaryResponse(
                job.getId(),
                job.getSymbol(),
                review.action().name(),
                review.confidenceScore(),
                review.riskLevel().name(),
                job.getCreatedAt()
        );
    }
}

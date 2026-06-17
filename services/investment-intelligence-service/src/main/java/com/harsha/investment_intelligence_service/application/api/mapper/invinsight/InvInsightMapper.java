package com.harsha.investment_intelligence_service.application.api.mapper.invinsight;

import com.harsha.contracts.dto.invintelligence.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invintelligence.invinsight.InvestmentInsightDetailResponse;
import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import org.springframework.stereotype.Component;

@Component
public class InvInsightMapper {
    public InvInsightSummaryResponse toSummaryResponse(
            AiReasoningJob job
    ) {
        InvestmentReview review =
                job.getParsedReview();

        return new InvInsightSummaryResponse(
                job.getId(),
                job.getSymbol(),
                job.getCompanyName(),
                review.action().name(),
                review.confidenceScore(),
                review.riskLevel().name(),
                job.getUpdatedAt()
        );
    }

    public InvestmentInsightDetailResponse toDetailedResponse(
            AiReasoningJob job
    ) {
        InvestmentReview review =
                job.getParsedReview();

        return new InvestmentInsightDetailResponse(
                job.getId(),
                job.getSymbol(),
                job.getCompanyName(),
                review.action().name(),
                review.confidenceScore(),
                review.riskLevel().name(),
                review.executiveSummary(),
                review.marketReasoning(),
                review.actionReasoning(),
                review.confidenceReasoning(),
                review.supportingFactors(),
                review.risks(),
                review.invalidationConditions(),
                job.getUpdatedAt()
        );
    }
}

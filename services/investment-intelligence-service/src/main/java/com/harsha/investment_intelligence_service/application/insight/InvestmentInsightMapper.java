package com.harsha.investment_intelligence_service.application.insight;

import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ProviderType;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InvestmentInsightMapper {
    public InvestmentInsightGeneratedEvent map(
            InvestmentReview investmentReview,
            String symbol,
            ProviderType providerType,
            String model
    ) {
        return new InvestmentInsightGeneratedEvent(
                symbol,
                Instant.now().toEpochMilli(),

                investmentReview.executiveSummary(),

                investmentReview.sentiment(),
                investmentReview.marketReasoning(),

                investmentReview.action(),
                investmentReview.actionReasoning(),

                investmentReview.timeHorizon(),

                investmentReview.expectedDirection(),
                investmentReview.expectedMagnitude(),
                investmentReview.marketBehaviorJustification(),

                investmentReview.supportingFactors(),
                investmentReview.risks(),
                investmentReview.contextLimitations(),
                investmentReview.invalidationConditions(),

                investmentReview.riskLevel(),
                investmentReview.riskJustification(),

                investmentReview.confidenceScore(),
                investmentReview.confidenceReasoning(),

                providerType.name(),
                model
        );
    }
}

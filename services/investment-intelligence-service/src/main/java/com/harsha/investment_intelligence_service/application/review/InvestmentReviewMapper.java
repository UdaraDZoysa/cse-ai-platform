package com.harsha.investment_intelligence_service.application.review;

import com.harsha.contracts.events.investment_intelligence.enums.*;
import com.harsha.investment_intelligence_service.application.reasoning.response.EnumNormalizer;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReviewRaw;
import org.springframework.stereotype.Component;

@Component
public class InvestmentReviewMapper {
    private final EnumNormalizer normalizer;

    public InvestmentReviewMapper(
            EnumNormalizer normalizer
    ) {
        this.normalizer = normalizer;
    }

    public InvestmentReview map(
            InvestmentReviewRaw raw
    ) {

        return new InvestmentReview(

                raw.executiveSummary(),

                normalizer.normalize(
                        raw.marketAssessment().sentiment(),
                        MarketSentiment.class,
                        "marketAssessment.sentiment"
                ),

                raw.marketAssessment().reasoning(),

                normalizer.normalize(
                        raw.recommendedAction().action(),
                        RecommendedAction.class,
                        "recommendedAction.action"
                ),

                raw.recommendedAction().reasoning(),

                normalizer.normalize(
                        raw.timeHorizon(),
                        TimeHorizon.class,
                        "timeHorizon"
                ),

                normalizer.normalize(
                        raw.expectedMarketBehavior().direction(),
                        ExpectedDirection.class,
                        "expectedMarketBehavior.direction"
                ),

                normalizer.normalize(
                        raw.expectedMarketBehavior().magnitude(),
                        ExpectedMagnitude.class,
                        "expectedMarketBehavior.magnitude"
                ),

                raw.expectedMarketBehavior().justification(),

                raw.supportingFactors(),

                raw.risks(),

                raw.contextLimitations(),

                raw.invalidationConditions(),

                normalizer.normalize(
                        raw.riskLevel().level(),
                        RiskLevel.class,
                        "riskLevel.level"
                ),

                raw.riskLevel().justification(),

                raw.confidence().score(),

                raw.confidence().reasoning()
        );
    }
}

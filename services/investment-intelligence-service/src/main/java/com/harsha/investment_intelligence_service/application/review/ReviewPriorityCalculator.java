package com.harsha.investment_intelligence_service.application.review;

import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.model.review.ReviewPriority;
import org.springframework.stereotype.Component;

@Component
public class ReviewPriorityCalculator {
    public ReviewPriority calculate(
            SymbolContext context
    ) {

        int score = 0;

        if (!context.activeInsights().isEmpty()) {
            score += 30;
        }

        if (!context.transitionHistory().isEmpty()) {
            score += 25;
        }

        if (!context.strategyHistory().isEmpty()) {

            var latest =
                    context.strategyHistory()
                            .getLast();

            if (latest.confidence() >= 80) {
                score += 20;
            }

            if (latest.persistence() >= 5) {
                score += 15;
            }
        }

        return new ReviewPriority(
                context.symbol(),
                score
        );
    }
}

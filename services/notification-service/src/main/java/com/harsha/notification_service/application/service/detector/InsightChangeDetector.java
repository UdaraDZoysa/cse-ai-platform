package com.harsha.notification_service.application.service.detector;

import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.notification_service.domain.entity.InsightState;
import com.harsha.notification_service.domain.model.InsightChange;
import org.springframework.stereotype.Component;

@Component
public class InsightChangeDetector {

    public InsightChange detect(
            InsightState previous,
            InvestmentInsightGeneratedEvent current
    ) {
        if (previous == null) {
            return new InsightChange(
                    true,
                    true,
                    true,
                    current.confidenceScore()
            );
        }

        return new InsightChange(
                previous.getAction() != current.action(),
                previous.getSentiment() != current.sentiment(),
                previous.getRiskLevel() != current.riskLevel(),
                current.confidenceScore() - previous.getConfidenceScore()
        );
    }
}

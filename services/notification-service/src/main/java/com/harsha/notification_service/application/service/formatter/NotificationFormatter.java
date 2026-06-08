package com.harsha.notification_service.application.service.formatter;

import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.notification_service.domain.model.NotificationMessage;
import com.harsha.notification_service.domain.model.NotificationPriority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class NotificationFormatter {
    public NotificationMessage format(
            InvestmentInsightGeneratedEvent event,
            NotificationPriority priority
    ) {

        String risks = event.risks().isEmpty()
                ? "• None identified"
                : event.risks().stream()
                .map(risk -> "• " + risk)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("• None identified");

        String body = """
                📈 <b>%s</b>
                
                🎯 <b>Recommendation:</b> %s
                
                %s <b>Opportunity Score:</b> %s (%d/100)
                
                ⚠️ <b>Risk:</b> %s
                
                📝 <b>Summary</b>
                %s
                
                🚨 <b>Key Risks</b>
                %s
                
                🧠 <b>Reasoning</b>
                %s
                
                🕑 <b>Generated:</b> %s
                
                --------------------
                Opportunity Score measures the overall strength of the current investment opportunity.
                Higher scores indicate stronger market and strategy signals.
                """
                .formatted(
                        event.symbol(),
                        event.action(),
                        strengthEmoji(event.confidenceScore()),
                        strengthLabel(
                                event.confidenceScore()
                        ),
                        event.confidenceScore(),
                        event.riskLevel(),
                        event.executiveSummary(),
                        risks,
                        event.marketReasoning(),
                        formatTimestamp(event.occurredAt())
                );

        return new NotificationMessage(
                event.symbol(),
                priority,
                "Investment Update",
                body
        );
    }

    private String strengthLabel(
            int score
    ) {

        if (score <= 20) {
            return "VERY WEAK";
        }

        if (score <= 40) {
            return "WEAK";
        }

        if (score <= 60) {
            return "MODERATE";
        }

        if (score <= 80) {
            return "STRONG";
        }

        return "VERY STRONG";
    }

    private String strengthEmoji(int score) {

        if (score <= 20) {
            return "🔴";
        }

        if (score <= 40) {
            return "🟠";
        }

        if (score <= 60) {
            return "🟡";
        }

        if (score <= 80) {
            return "🟢";
        }

        return "🚀";
    }

    private String formatTimestamp(
            long epochMillis
    ) {

        return Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.of("Asia/Colombo"))
                .format(
                        DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd HH:mm"
                        )
                );
    }
}

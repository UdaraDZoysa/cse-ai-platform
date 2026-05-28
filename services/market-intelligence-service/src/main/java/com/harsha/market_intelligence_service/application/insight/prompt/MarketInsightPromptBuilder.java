package com.harsha.market_intelligence_service.application.insight.prompt;

import com.harsha.market_intelligence_service.domain.insight.model.InsightGenerationContext;
import org.springframework.stereotype.Component;

@Component
public class MarketInsightPromptBuilder {
    public String build(
            InsightGenerationContext context
    ) {

        StringBuilder builder =
                new StringBuilder();

        builder.append("""
                You are a financial market intelligence analyst.

                Analyze the following narrative context and generate structured market intelligence.

                Return ONLY valid JSON.

                Required JSON structure:
                {
                  "summary": "...",
                  "reasoning": "...",
                  "sentiment": "BULLISH|BEARISH|NEUTRAL",
                  "importanceScore": 0.0,
                  "persistenceScore": 0.0,
                  "confidenceScore": 0.0
                }

                SYMBOL:
                """);

        builder.append(context.symbol())
                .append("\n");

        builder.append("""
                COMPANY:
                """);

        builder.append(context.company())
                .append("\n");

        builder.append("""
                EXISTING NARRATIVE:
                """);

        builder.append(
                context.narrative()
                        .getSummary()
        ).append("\n");

        builder.append("""
                SOURCES:
                """);

        for (var source : context.sources()) {

            builder.append("""
                    
                    TITLE:
                    """);

            builder.append(source.getTitle())
                    .append("\n");

            builder.append("""
                    CONTENT:
                    """);

            builder.append(source.getContent())
                    .append("\n");
        }

        return builder.toString();
    }
}

package com.harsha.market_intelligence_service.application.insight.prompt;

import com.harsha.market_intelligence_service.domain.insight.model.InsightGenerationContext;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeSource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Component
public class MarketInsightPromptBuilder {
    private static final int MAX_PROMPT_CHARS = 12_000;
    private static final Comparator<NarrativeSource> SOURCE_FRESHNESS_COMPARATOR =
            Comparator.comparing(
                    (NarrativeSource source) ->
                            source.getIntelligence() == null
                                    ? null
                                    : source.getIntelligence().getGeneratedAt(),
                    Comparator.nullsLast(
                            Comparator.reverseOrder()
                    )
            ).thenComparing(
                    NarrativeSource::getPublishedDate,
                    Comparator.nullsLast(
                            Comparator.reverseOrder()
                    )
            );

    public String build(
            InsightGenerationContext context
    ) {

        StringBuilder builder =
                new StringBuilder();

        appendHeader(builder, context);

        List<NarrativeSource> sortedSources = context.sources()
                .stream()
                .sorted(SOURCE_FRESHNESS_COMPARATOR)
                .toList();

        for (NarrativeSource source : sortedSources) {
            String sourceBlock = buildSourceBlock(source);

            int projectedLength = builder.length() + sourceBlock.length();

            if (projectedLength > MAX_PROMPT_CHARS) {
                break;
            }
            builder.append(sourceBlock);
        }
        return builder.toString();
    }

    private void appendHeader(
            StringBuilder builder,
            InsightGenerationContext context
    ) {

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
    }

    private String buildSourceBlock(
            NarrativeSource source
    ) {

        return """
                
                TITLE:
                %s
                PUBLISHED_AT:
                %s
                CONTENT:
                %s
                
                """
                .formatted(
                        nullToBlank(source.getTitle()),
                        formatPublishedDate(source.getPublishedDate()),
                        nullToBlank(source.getContent())
                );
    }

    private String nullToBlank(
            String value
    ) {
        return value == null
                ? ""
                : value;
    }

    private String formatPublishedDate(
            Instant publishedDate
    ) {
        return publishedDate == null
                ? ""
                : publishedDate.toString();
    }
}

package com.harsha.market_intelligence_service.infrastructure.ai.groq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.market_intelligence_service.application.insight.port.NarrativeSynthesisClient;
import com.harsha.market_intelligence_service.domain.insight.model.MarketInsightResult;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GroqNarrativeSynthesisClient implements NarrativeSynthesisClient {
    private final RestClient restClient;
    private final ObjectMapper mapper;
    private final String model;

    public GroqNarrativeSynthesisClient(
            @Value("${groq.api.key}") String apiKey,
            ObjectMapper mapper,
            @Value("${groq.model}") String model) {
        this.mapper = mapper;
        this.model = model;
        this.restClient =
                RestClient.builder()
                        .baseUrl("https://api.groq.com/openai/v1")
                        .defaultHeader(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + apiKey
                        )
                        .defaultHeader(
                                HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                        .build();
    }

    @Override
    public MarketInsightResult synthesize(
            String prompt
    ) {
        try {
            Map<String, Object> request =
                    Map.of(
                            "model", model,
                            "temperature", 0.2,
                            "messages", List.of(
                                    Map.of(
                                            "role", "user",
                                            "content", prompt
                                    )
                            ),
                            "response_format",
                            Map.of(
                                    "type", "json_object"
                            )
                    );

            String response =
                    restClient.post()
                            .uri("/chat/completions")
                            .body(request)
                            .retrieve()
                            .body(String.class);

            String content =
                    mapper.readTree(response)
                            .get("choices")
                            .get(0)
                            .get("message")
                            .get("content")
                            .asText();

            MarketInsightResult result =
                    mapper.readValue(
                            content,
                            MarketInsightResult.class
                    );

            validate(result);
            return result;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Narrative synthesis failed",
                    e
            );
        }
    }

    private void validate(
            MarketInsightResult result
    ) {

        if (result == null) {
            throw new IllegalStateException(
                    "Empty synthesis result"
            );
        }

        if (result.sentiment() == null) {
            throw new IllegalStateException(
                    "Invalid sentiment"
            );
        }

        if (result.summary() == null
                || result.summary().isBlank()
        ) {
            throw new IllegalStateException(
                    "Missing summary"
            );
        }

        if (result.reasoning() == null
                || result.reasoning().isBlank()
        ) {
            throw new IllegalStateException(
                    "Missing reasoning"
            );
        }

        validateScore(
                result.importanceScore(),
                "importanceScore"
        );

        validateScore(
                result.persistenceScore(),
                "persistenceScore"
        );

        validateScore(
                result.confidenceScore(),
                "confidenceScore"
        );
    }

    private void validateScore(
            double value,
            String field
    ) {

        if (Double.isNaN(value)
                || value < 0.0 || value > 1.0
        ) {
            throw new IllegalStateException(
                    "Invalid score for: " + field
            );
        }
    }
}

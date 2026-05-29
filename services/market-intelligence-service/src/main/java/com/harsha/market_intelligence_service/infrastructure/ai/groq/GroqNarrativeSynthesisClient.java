package com.harsha.market_intelligence_service.infrastructure.ai.groq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.market_intelligence_service.application.insight.port.NarrativeSynthesisClient;
import com.harsha.market_intelligence_service.domain.insight.exception.InsightValidationException;
import com.harsha.market_intelligence_service.domain.insight.exception.InvalidAiResponseException;
import com.harsha.market_intelligence_service.domain.insight.exception.NonRetryableAiException;
import com.harsha.market_intelligence_service.domain.insight.exception.RetryableAiException;
import com.harsha.market_intelligence_service.domain.insight.model.AiProcessErrorType;
import com.harsha.market_intelligence_service.domain.insight.model.MarketInsightResult;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
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

        } catch (HttpClientErrorException.TooManyRequests ex) {
            throw new RetryableAiException(
                    "Groq rate limit exceeded",
                    AiProcessErrorType.RATE_LIMIT,
                    ex
            );

        } catch (HttpServerErrorException ex) {
            throw new RetryableAiException(
                    "Groq service unavailable",
                    AiProcessErrorType.PROVIDER_UNAVAILABLE,
                    ex
            );

        } catch (ResourceAccessException ex) {
            throw new RetryableAiException(
                    "Network failure",
                    AiProcessErrorType.NETWORK_ERROR,
                    ex
            );

        } catch (JsonProcessingException ex) {
            throw new InvalidAiResponseException(
                    "Failed to parse AI response",
                    AiProcessErrorType.INVALID_RESPONSE,
                    ex
            );

        } catch (InsightValidationException ex) {
            throw ex;

        } catch (HttpClientErrorException ex) {
            throw new NonRetryableAiException(
                    "Invalid AI request",
                    AiProcessErrorType.NON_RETRYABLE,
                    ex
            );
        } catch (Exception ex) {
            throw new RetryableAiException(
                    "Unexpected AI failure",
                    AiProcessErrorType.UNKNOWN,
                    ex
            );
        }
    }

    private void validate(
            MarketInsightResult result
    ) {

        if (result == null) {
            throw new InsightValidationException(
                    "Empty synthesis result"
            );
        }

        if (result.sentiment() == null) {
            throw new InsightValidationException(
                    "Invalid sentiment"
            );
        }

        if (result.summary() == null
                || result.summary().isBlank()
        ) {
            throw new InsightValidationException(
                    "Missing summary"
            );
        }

        if (result.reasoning() == null
                || result.reasoning().isBlank()
        ) {
            throw new InsightValidationException(
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
            throw new InsightValidationException(
                    "Invalid score for: " + field
            );
        }
    }
}

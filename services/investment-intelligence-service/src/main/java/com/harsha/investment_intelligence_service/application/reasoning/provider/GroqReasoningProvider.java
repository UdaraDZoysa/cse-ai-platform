package com.harsha.investment_intelligence_service.application.reasoning.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob.ProviderRateLimitState;
import com.harsha.investment_intelligence_service.config.LlmProperties;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ProviderType;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ReasoningRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.ReasoningResponse;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.exception.ai.InvalidResponseException;
import com.harsha.investment_intelligence_service.exception.ai.NonRetryableAIException;
import com.harsha.investment_intelligence_service.exception.ai.RetryableAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GroqReasoningProvider implements ReasoningProvider{
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final ProviderRateLimitState rateLimitState;
    private static final Logger log = LoggerFactory.getLogger(GroqReasoningProvider.class);


    public GroqReasoningProvider(
            ObjectMapper objectMapper,
            LlmProperties llmProperties,
            ProviderRateLimitState rateLimitState
    ) {
        this.objectMapper = objectMapper;
        this.rateLimitState = rateLimitState;

        this.restClient = RestClient.builder()
                .baseUrl(llmProperties.baseUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + llmProperties.apiKey()
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    @Override
    public ProviderType providerType() {
        return ProviderType.GROQ;
    }

    @Override
    public ReasoningResponse generate(ReasoningRequest request) {
        if (rateLimitState.isBlocked()) {

            throw new RetryableAIException(
                    """
                    Groq provider temporarily blocked due to previous
                    rate limit response.
                    """,
                    ProcessingErrorType.RATE_LIMIT,
                    null
            );
        }

        try {
            Map<String, Object> aiRequest =
                    Map.of(
                            "model", request.model(),
                            "temperature", 0.2,
                            "messages", List.of(
                                    Map.of(
                                            "role", "user",
                                            "content", request.prompt()
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
                            .body(aiRequest)
                            .retrieve()
                            .body(String.class);

            String content =
                    objectMapper.readTree(response)
                            .get("choices")
                            .get(0)
                            .get("message")
                            .get("content")
                            .asText();

            log.info(
                    """
                    GROQ CONTENT
                    
                    {}
                    """,
                    content
            );

            return new ReasoningResponse(
                    content
            );

        } catch (HttpClientErrorException.TooManyRequests ex) {
            log.error(
                    """
                    GROQ RATE LIMIT
                    
                    Status: {}
                    Headers: {}
                    Body: {}
                    """,
                    ex.getStatusCode(),
                    ex.getResponseHeaders(),
                    ex.getResponseBodyAsString()
            );

            rateLimitState.blockForMinutes(1);

            throw new RetryableAIException(
                    "Groq rate limit exceeded",
                    ProcessingErrorType.RATE_LIMIT,
                    ex
            );

        } catch (HttpServerErrorException ex) {
            throw new RetryableAIException(
                    "Groq service unavailable",
                    ProcessingErrorType.PROVIDER_UNAVAILABLE,
                    ex
            );

        } catch (ResourceAccessException ex) {
            throw new RetryableAIException(
                    "Network failure",
                    ProcessingErrorType.NETWORK_ERROR,
                    ex
            );

        } catch (JsonProcessingException ex) {
            throw new InvalidResponseException(
                    "Failed to parse AI response",
                    ex
            );

        } catch (HttpClientErrorException ex) {
            throw new NonRetryableAIException(
                    "Invalid AI request",
                    ProcessingErrorType.NON_RETRYABLE,
                    ex
            );
        } catch (Exception ex) {
            throw new RetryableAIException(
                    "Unexpected AI failure",
                    ProcessingErrorType.UNKNOWN,
                    ex
            );
        }
    }
}

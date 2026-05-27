package com.harsha.market_intelligence_service.narrative.client;

import com.harsha.market_intelligence_service.narrative.config.ExaProperties;
import com.harsha.market_intelligence_service.narrative.dto.NarrativeExtractionResult;
import com.harsha.market_intelligence_service.narrative.dto.WebSearchResult;
import com.harsha.market_intelligence_service.narrative.dto.exa.ExaSearchRequest;
import com.harsha.market_intelligence_service.narrative.dto.exa.ExaSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Component
public class ExaWebSearchClient implements WebSearchClient {
    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(ExaWebSearchClient.class);

    public ExaWebSearchClient(ExaProperties properties) {
        this.webClient = WebClient
                .builder()
                .baseUrl(
                        properties.baseUrl()
                )
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + properties.apiKey()
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    @Override
    public NarrativeExtractionResult search(
            String symbol,
            String companyName) {

        String query = """
                Latest important financial, strategic,
                investment, operational, earnings,
                partnership, acquisition, expansion,
                and market developments related to
                %s (%s) in Sri Lanka
                """
                .formatted(companyName, symbol)
                .replaceAll("\\s+", " ")
                .trim();

        Map<String, Object> schema =
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "summary", Map.of(
                                        "type",
                                        "string"
                                ),
                                "majorDevelopments", Map.of(
                                        "type",
                                        "array",
                                        "items", Map.of(
                                                "type",
                                                "string"
                                        )
                                ),

                                "strategicMoves", Map.of(
                                        "type",
                                        "array",
                                        "items", Map.of(
                                                "type",
                                                "string"
                                        )
                                ),

                                "risks", Map.of(
                                        "type",
                                        "array",
                                        "items", Map.of(
                                                "type",
                                                "string"
                                        )
                                )
                        )
                );

        ExaSearchRequest request = new ExaSearchRequest(
                query,
                "auto",
                5,
                """
                        You are a financial
                        intelligence analyst.

                        Focus on:
                        - earnings
                        - strategic developments
                        - investments
                        - risks
                        - expansions
                        - acquisitions
                        - operational changes

                        Keep output factual
                        and grounded.
                        """,
                schema,
                Map.of(
                        "highlights",
                        true
                )
        );

        ExaSearchResponse response;

        try {
            response = webClient
                    .post()
                    .uri("/search")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ExaSearchResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {

            int status = ex.getStatusCode().value();

            if (status == 402) {
                log.warn(
                        "Exa quota exceeded for symbol={}",
                        symbol
                );
                return emptyResult(symbol, companyName);
            }

            if (status == 429) {
                log.warn(
                        "Exa rate limit exceeded for symbol={}",
                        symbol
                );
                return emptyResult(symbol, companyName);
            }

            log.error(
                    "Exa API failed. status={} symbol={}",
                    status,
                    symbol,
                    ex
            );

            return emptyResult(symbol, companyName);

        } catch (Exception ex) {
            log.error(
                    "Unexpected Exa client failure for symbol={}",
                    symbol,
                    ex
            );
            return emptyResult(symbol, companyName);
        }

        if (response == null) {
            return emptyResult(symbol, companyName);
        }

        List<WebSearchResult> source = response
                .results()
                .stream()
                .map(r ->
                        new WebSearchResult(
                                r.title(),
                                r.highlights() == null
                                        ? ""
                                        : String.join(
                                                "\n",
                                                r.highlights()
                                        ),
                                r.url(),
                                r.publishedDate()
                        )
                )
                .toList();

        String summary = response
                .output() == null
                    || response.output().content() == null
                ? ""
                : response.output().content().summary();

        return new NarrativeExtractionResult(
                symbol,
                companyName,
                summary,
                source
        );
    }
    private NarrativeExtractionResult emptyResult(
            String symbol,
            String companyName
    ) {

        return new NarrativeExtractionResult(
                symbol,
                companyName,
                "",
                List.of()
        );
    }
}

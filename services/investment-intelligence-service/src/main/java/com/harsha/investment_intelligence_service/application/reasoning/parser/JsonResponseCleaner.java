package com.harsha.investment_intelligence_service.application.reasoning.parser;

import com.harsha.investment_intelligence_service.exception.ai.AIResponseParseException;
import org.springframework.stereotype.Component;

@Component
public class JsonResponseCleaner {
    public String clean(String response) {
        if (response == null || response.isBlank()) {
            throw new AIResponseParseException(
                    "Empty AI response",
                    null
            );
        }

        String cleanedResponse = response
                .replace("```json", "")
                .replace("```JSON", "")
                .replace("```", "")
                .trim();

        int start = cleanedResponse.indexOf("{");
        int end = cleanedResponse.lastIndexOf('}');

        if (start < 0 || end < 0 || end <= start) {
            throw new AIResponseParseException(
                    "No valid JSON object found in response",
                    null
            );
        }

        return cleanedResponse.substring(
                start,
                end + 1
        );
    }
}

package com.harsha.investment_intelligence_service.application.reasoning.response;

import com.harsha.investment_intelligence_service.exception.ai.ResponseValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnumNormalizer {
    private static final Logger log = LoggerFactory.getLogger(EnumNormalizer.class);

    private static final Map<String, String> ALIASES =
            Map.ofEntries(

                    Map.entry("SIDEWAY", "SIDEWAYS"),
                    Map.entry("SIDWAY", "SIDEWAYS"),
                    Map.entry("SIDESWAY", "SIDEWAYS"),

                    Map.entry("SHORTTERM", "SHORT_TERM"),
                    Map.entry("MEDIUMTERM", "MEDIUM_TERM"),
                    Map.entry("LONGTERM", "LONG_TERM"),

                    Map.entry("MODERAT", "MODERATE")
            );

    public <T extends Enum<T>> T normalize(
            String value,
            Class<T> enumType,
            String field
    ) {

        if (value == null || value.isBlank()) {
            throw new ResponseValidationException(
                    field + " missing"
            );
        }

        String original = value;

        String normalized =
                value.trim()
                        .toUpperCase()
                        .replace("-", "_")
                        .replace(" ", "_");

        normalized =
                ALIASES.getOrDefault(
                        normalized,
                        normalized
                );

        try {
            return Enum.valueOf(
                    enumType,
                    normalized
            );
        } catch (Exception ignored) {
        }

        T recovered =
                recover(
                        normalized,
                        enumType
                );

        log.warn(
                "Recovered enum field={} original={} corrected={}",
                field,
                original,
                recovered
        );

        return recovered;
    }

    private <T extends Enum<T>> T recover(
            String value,
            Class<T> enumType
    ) {

        T bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;

        for (T constant : enumType.getEnumConstants()) {

            int distance =
                    levenshtein(
                            value,
                            constant.name()
                    );

            if (distance < bestDistance) {
                bestDistance = distance;
                bestMatch = constant;
            }
        }

        if (bestDistance <= 2) {
            return bestMatch;
        }

        throw new ResponseValidationException(
                "Cannot recover enum value: "
                        + value
        );
    }

    private int levenshtein(
            String a,
            String b
    ) {

        int[][] dp =
                new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {

            for (int j = 1; j <= b.length(); j++) {

                int cost =
                        a.charAt(i - 1)
                                == b.charAt(j - 1)
                                ? 0
                                : 1;

                dp[i][j] =
                        Math.min(
                                Math.min(
                                        dp[i - 1][j] + 1,
                                        dp[i][j - 1] + 1
                                ),
                                dp[i - 1][j - 1] + cost
                        );
            }
        }

        return dp[a.length()][b.length()];
    }
}

package com.harsha.market_intelligence_service.shared.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TimeParser {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy hh:mm:ss a"
            );

    private static final ZoneId DEFAULT_ZONE =
            ZoneId.of("Asia/Colombo");

    private TimeParser() {

    }

    public static Instant parseToInstant(
            String value
    ) {

        try {
            return Instant.parse(value);

        } catch (DateTimeParseException ignored) {

        }

        try {
            return LocalDateTime
                    .parse(value, FORMATTER)
                    .atZone(DEFAULT_ZONE)
                    .toInstant();

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "Unsupported datetime format: " + value,
                    e
            );
        }
    }
}

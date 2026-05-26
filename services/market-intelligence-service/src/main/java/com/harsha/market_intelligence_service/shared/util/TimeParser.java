package com.harsha.market_intelligence_service.shared.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeParser {
    public static Instant parseToInstant(
            String value
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd MMM yyyy hh:mm:ss a"
        );

        try{
            return LocalDateTime
                    .parse(value, formatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

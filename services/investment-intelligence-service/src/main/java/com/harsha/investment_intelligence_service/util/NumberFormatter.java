package com.harsha.investment_intelligence_service.util;

public final class NumberFormatter {
    private NumberFormatter() {
    }

    public static String humanReadable(double value) {

        if (value >= 1_000_000_000_000.0) {
            return "%.2f Trillion".formatted(
                    value / 1_000_000_000_000.0
            );
        }

        if (value >= 1_000_000_000.0) {
            return "%.2f Billion".formatted(
                    value / 1_000_000_000.0
            );
        }

        if (value >= 1_000_000.0) {
            return "%.2f Million".formatted(
                    value / 1_000_000.0
            );
        }

        if (value >= 1_000.0) {
            return "%.2f Thousand".formatted(
                    value / 1_000.0
            );
        }

        return "%.2f".formatted(value);
    }
}

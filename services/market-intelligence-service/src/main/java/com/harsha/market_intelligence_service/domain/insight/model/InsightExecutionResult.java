package com.harsha.market_intelligence_service.domain.insight.model;

public record InsightExecutionResult(
        boolean generated,
        String reason
) {
    public static InsightExecutionResult insightGenerated() {
        return new InsightExecutionResult(
                true,
                "GENERATED"
        );
    }

    public static InsightExecutionResult skipped(
            String reason
    ) {
        return new InsightExecutionResult(
                false,
                reason
        );
    }
}
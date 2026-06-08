package com.harsha.notification_service.domain.model;

public record InsightChange(
        boolean actionChanged,
        boolean sentimentChanged,
        boolean riskChanged,
        int confidenceDelta
) {
}

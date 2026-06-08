package com.harsha.notification_service.domain.model;

public record NotificationMessage(
        String symbol,
        NotificationPriority priority,
        String title,
        String body
) {
}

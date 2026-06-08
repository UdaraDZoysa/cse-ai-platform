package com.harsha.notification_service.domain.model;

public record NotificationDecision(
        boolean notifiable,
        NotificationPriority priority,
        String reason
) {
}

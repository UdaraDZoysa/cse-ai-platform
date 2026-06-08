package com.harsha.notification_service.domain.model;

import java.time.Instant;

public record NotificationJobRetryScheduled(
        Instant nextAttemptAt
) {
}

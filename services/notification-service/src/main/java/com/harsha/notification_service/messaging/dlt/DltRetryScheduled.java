package com.harsha.notification_service.messaging.dlt;

import java.time.Instant;

public record DltRetryScheduled(
        Instant nextAttemptAt
) {
}

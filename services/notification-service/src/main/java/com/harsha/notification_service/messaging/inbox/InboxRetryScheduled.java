package com.harsha.notification_service.messaging.inbox;

import java.time.Instant;

public record InboxRetryScheduled(
        Instant nextAttemptAt
) {
}

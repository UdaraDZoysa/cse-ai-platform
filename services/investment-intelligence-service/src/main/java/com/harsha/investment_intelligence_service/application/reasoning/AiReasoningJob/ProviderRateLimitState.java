package com.harsha.investment_intelligence_service.application.reasoning.AiReasoningJob;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class ProviderRateLimitState {
    private volatile Instant blockedUntil;

    public void blockForMinutes(long minutes) {
        blockedUntil = Instant.now()
                .plusSeconds(minutes * 60);
    }

    public boolean isBlocked() {
        return blockedUntil != null
                && Instant.now().isBefore(blockedUntil);
    }

    public long remainingMillis() {
        if (!isBlocked()) {
            return 0;
        }

        return Math.max(
                0,
                Duration.between(
                        Instant.now(),
                        blockedUntil
                ).toMillis()
        );
    }
}

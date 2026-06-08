package com.harsha.notification_service.application.service.evaluator;

import com.harsha.notification_service.domain.model.InsightChange;
import com.harsha.notification_service.domain.model.NotificationDecision;
import com.harsha.notification_service.domain.model.NotificationPriority;
import org.springframework.stereotype.Component;

@Component
public class NotificationEvaluator {
    public NotificationDecision evaluate(
            InsightChange change
    ) {

        int score = 0;

        if (change.actionChanged()) {
            score += 50;
        }

        if (change.sentimentChanged()) {
            score += 30;
        }

        if (change.riskChanged()) {
            score += 20;
        }

        if (Math.abs(change.confidenceDelta()) >= 20) {
            score += 20;
        }

        if (score >= 90) {
            return new NotificationDecision(
                    true,
                    NotificationPriority.CRITICAL,
                    "Major investment signal change"
            );
        }

        if (score >= 60) {
            return new NotificationDecision(
                    true,
                    NotificationPriority.HIGH,
                    "Important signal change"
            );
        }

        if (score >= 30) {
            return new NotificationDecision(
                    true,
                    NotificationPriority.MEDIUM,
                    "Moderate signal change"
            );
        }

        return new NotificationDecision(
                true,
                NotificationPriority.LOW,
                "Minor update"
        );
    }
}

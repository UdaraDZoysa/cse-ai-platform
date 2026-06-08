package com.harsha.notification_service.application.service.sender;

import com.harsha.notification_service.domain.model.NotificationChannel;
import com.harsha.notification_service.domain.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogNotificationSender
        implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(LogNotificationSender.class);

    @Override
    public NotificationChannel channel() {
        return null;
    }

    @Override
    public void send(
            NotificationMessage message
    ) {
        log.info(
                """

                ============================
                NOTIFICATION
                ============================

                Priority: {}

                {}

                """,
                message.priority(),
                message.body()
        );
    }
}

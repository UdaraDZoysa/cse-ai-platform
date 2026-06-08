package com.harsha.notification_service.application.service.sender;

import com.harsha.notification_service.domain.model.NotificationChannel;
import com.harsha.notification_service.domain.model.NotificationMessage;

public interface NotificationSender {
    NotificationChannel channel();
    void send(
            NotificationMessage message
    );
}

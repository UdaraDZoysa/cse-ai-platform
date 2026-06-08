package com.harsha.notification_service.application.service.sender;

import com.harsha.notification_service.config.TelegramProperties;
import com.harsha.notification_service.domain.model.NotificationChannel;
import com.harsha.notification_service.domain.model.NotificationMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class TelegramNotificationSender implements NotificationSender {
    private final TelegramProperties properties;
    private final RestClient restClient;

    public TelegramNotificationSender(
            TelegramProperties properties
    ) {
        this.properties = properties;
        this.restClient =
                RestClient.builder()
                        .baseUrl(
                                "https://api.telegram.org"
                        )
                        .build();
    }


    @Override
    public NotificationChannel channel() {
        return NotificationChannel.TELEGRAM;
    }

    @Override
    public void send(
            NotificationMessage message
    ) {
        for (String chatId : properties.chatIds()) {

            Map<String, Object> request = Map.of(
                    "chat_id", chatId,
                    "text", message.body(),
                    "parse_mode", "HTML"
            );

            restClient.post()
                    .uri("/bot%s/sendMessage"
                            .formatted(properties.botToken())
                    )
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        }
    }
}

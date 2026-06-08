package com.harsha.notification_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        String botToken,
        List<String> chatIds
) {
}

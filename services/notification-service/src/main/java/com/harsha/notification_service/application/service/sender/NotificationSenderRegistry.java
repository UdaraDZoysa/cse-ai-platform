package com.harsha.notification_service.application.service.sender;

import com.harsha.notification_service.domain.model.NotificationChannel;
import com.harsha.notification_service.exception.NonRetryableProcessingException;
import com.harsha.notification_service.exception.ProcessingErrorType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationSenderRegistry {
    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationSenderRegistry(
            List<NotificationSender> senders
    ) {
        this.senders = senders.stream()
                .collect(
                        Collectors.toMap(
                                NotificationSender::channel,
                                Function.identity()
                        )
                );
    }

    private Collection<NotificationSender> allSenders() {
        return senders.values();
    }

    public NotificationSender getSender(NotificationChannel channel) {
        NotificationSender sender =
                senders.get(channel);

        if (sender == null) {
            throw new NonRetryableProcessingException(
                    "No sender registered for channel: "
                            + channel,
                    ProcessingErrorType.SENDER_NOT_FOUND,
                    null
            );
        }

        return sender;
    }
}

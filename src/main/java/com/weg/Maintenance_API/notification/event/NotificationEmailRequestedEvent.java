package com.weg.Maintenance_API.notification.event;

import java.util.UUID;

public record NotificationEmailRequestedEvent(
        UUID notificationId,
        String recipientEmail,
        String title,
        String message
) {
}

package com.weg.Maintenance_API.notification.event;

import java.util.UUID;

// Executa a operacao deste metodo.
public record NotificationEmailRequestedEvent(
        UUID notificationId,
        String recipientEmail,
        String title,
        String message
) {
}

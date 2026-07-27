package com.weg.Maintenance_API.user.dto.response;

// Executa a operacao deste metodo.
public record NotificationPreferenceResponse(
        boolean emailEnabled,
        boolean inAppEnabled,
        boolean occurrenceNotifications,
        boolean purchaseNotifications,
        boolean inspectionNotifications
) {
}

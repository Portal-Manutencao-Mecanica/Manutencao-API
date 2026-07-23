package com.weg.Maintenance_API.user.dto.response;

public record NotificationPreferenceResponse(
        boolean emailEnabled,
        boolean inAppEnabled,
        boolean occurrenceNotifications,
        boolean purchaseNotifications,
        boolean inspectionNotifications
) {
}

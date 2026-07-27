package com.weg.Maintenance_API.user.dto.request;

public record UpdateNotificationPreferencesRequest(
        Boolean emailEnabled,
        Boolean inAppEnabled,
        Boolean occurrenceNotifications,
        Boolean purchaseNotifications,
        Boolean inspectionNotifications
) {

    public boolean isEmpty() {
        return emailEnabled == null
                && inAppEnabled == null
                && occurrenceNotifications == null
                && purchaseNotifications == null
                && inspectionNotifications == null;
    }
}

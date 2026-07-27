package com.weg.Maintenance_API.user.dto.request;

// Atualiza o estado conforme os dados informados.
public record UpdateNotificationPreferencesRequest(
        Boolean emailEnabled,
        Boolean inAppEnabled,
        Boolean occurrenceNotifications,
        Boolean purchaseNotifications,
        Boolean inspectionNotifications
) {

    // Valida a regra aplicada por este metodo.
    public boolean isEmpty() {
        return emailEnabled == null
                && inAppEnabled == null
                && occurrenceNotifications == null
                && purchaseNotifications == null
                && inspectionNotifications == null;
    }
}

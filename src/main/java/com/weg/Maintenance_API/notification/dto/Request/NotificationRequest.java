package com.weg.Maintenance_API.notification.dto.Request;

public record NotificationRequest(
    String email,
    String message
) {

}

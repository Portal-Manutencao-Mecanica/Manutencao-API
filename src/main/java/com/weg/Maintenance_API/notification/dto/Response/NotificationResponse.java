package com.weg.Maintenance_API.notification.dto.Response;

public record NotificationResponse (
    Long id,
    String email,
    String message
){

}

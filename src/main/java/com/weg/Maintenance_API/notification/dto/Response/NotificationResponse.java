package com.weg.Maintenance_API.notification.dto.Response;

public record NotificationResponse (
    Long id,
        String numberCard,
    String email,
    String title,
    String about,
    String description,
    Boolean statusRead
){

}

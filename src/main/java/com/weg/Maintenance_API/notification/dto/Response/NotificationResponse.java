package com.weg.Maintenance_API.notification.dto.Response;


import java.util.UUID;

public record NotificationResponse (
    UUID id,
        String numberCard,
    String email,
    String title,
    String about,
    String description,
    Boolean statusRead
){

}

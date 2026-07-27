package com.weg.Maintenance_API.notification.dto.Response;


import java.util.UUID;

// Executa a operacao deste metodo.
public record NotificationResponse (
    UUID id,
    String email,
    String title,
    String about,
    String description,
    Boolean statusRead
){

}

package com.weg.Maintenance_API.notification.mapper;

import org.springframework.stereotype.Component;

import com.weg.Maintenance_API.notification.dto.Request.NotificationRequest;
import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.entity.Notification;

@Component
public class NotificationMapper {

    // Converte os dados para o formato necessario.
    public Notification toEntity(NotificationRequest notificationRequest) {
        return new Notification(
                notificationRequest.email(),
                notificationRequest.title(),
                notificationRequest.about(),
                notificationRequest.description());
    }

    // Converte os dados para o formato necessario.
    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getEmail(),
                notification.getTitle(),
                notification.getAbout(),
                notification.getDescription(),
                notification.isStatusRead());
    }

}

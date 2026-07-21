package com.weg.Maintenance_API.notification.mapper;

import org.springframework.stereotype.Component;

import com.weg.Maintenance_API.notification.dto.Request.NotificationRequest;
import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.entity.Notification;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest notificationRequest) {
        return new Notification(notificationRequest.email(), notificationRequest.about(),
                notificationRequest.message());
    }

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getEmail(), notification.getAbout(), notification.getMessage());
    }

}

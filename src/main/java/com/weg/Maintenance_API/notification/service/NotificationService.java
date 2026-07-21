package com.weg.Maintenance_API.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.weg.Maintenance_API.notification.dto.Request.NotificationRequest;
import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.entity.Notification;
import com.weg.Maintenance_API.notification.mapper.NotificationMapper;
import com.weg.Maintenance_API.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationResponse create(NotificationRequest notificationRequest) {
        Notification notification = notificationMapper.toEntity(notificationRequest);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("portalmanutencaoweg@gmail.com");
        email.setTo(notificationRequest.email());
        email.setSubject(notificationRequest.about());
        email.setText(notification.getMessage());
        mailSender.send(email);
        return notificationMapper.toResponse(notification);
    }

}

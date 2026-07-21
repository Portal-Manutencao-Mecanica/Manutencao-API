package com.weg.Maintenance_API.notification.service;

import java.util.List;

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
        email.setSubject(notificationRequest.title());
        email.setText(notificationRequest.description());
        mailSender.send(email);
        notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    public List<NotificationResponse> getAll() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream().map(notificationMapper::toResponse).toList();
    }

    public NotificationResponse getById(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            throw new RuntimeException("Notificação não existe");
        }
        return notificationMapper.toResponse(notification);
    }

    public void delete(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification == null) {
            throw new RuntimeException("Notifiçao não existe");
        }
        notificationRepository.delete(notification);
    }

}

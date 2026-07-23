package com.weg.Maintenance_API.notification.service;


import java.util.UUID;

import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import com.weg.Maintenance_API.exception.type.NotificationDeliveryException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
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

    @Value("${app.mail.from}")
    private String mailFrom;

    @Transactional
    public NotificationResponse create(NotificationRequest notificationRequest) {
        Notification notification = notificationMapper.toEntity(notificationRequest);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(mailFrom);
        email.setTo(notificationRequest.email());
        email.setSubject(notificationRequest.title());
        email.setText(notificationRequest.description());

        try {
            mailSender.send(email);
        } catch (MailException exception) {
            throw new NotificationDeliveryException(exception);
        }

        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(UUID id) {
        return notificationMapper.toResponse(findById(id));
    }

    @Transactional
    public void delete(UUID id) {
        notificationRepository.delete(findById(id));
    }

    /** Marca a notificação como lida. */
    @Transactional
    public NotificationResponse readNotification(UUID id) {
        Notification notification = findById(id);
        notification.setStatusRead(true);
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    /** Alterna entre lida e não lida. */
    @Transactional
    public NotificationResponse toggleReadStatus(UUID id) {
        Notification notification = findById(id);
        notification.setStatusRead(!notification.isStatusRead());
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    /** Marca todas as notificações como lidas. */
    @Transactional
    public void markAllAsRead() {
        List<Notification> notifications = notificationRepository.findAll();
        notifications.forEach(notification -> notification.setStatusRead(true));
        notificationRepository.saveAll(notifications);
    }

    private Notification findById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação", id));
    }
}

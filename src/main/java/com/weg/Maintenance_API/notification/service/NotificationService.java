package com.weg.Maintenance_API.notification.service;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.notification.dto.Request.NotificationRequest;
import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.entity.Notification;
import com.weg.Maintenance_API.notification.event.NotificationEmailRequestedEvent;
import com.weg.Maintenance_API.notification.mapper.NotificationMapper;
import com.weg.Maintenance_API.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = notificationRepository.save(
                notificationMapper.toEntity(request)
        );

        eventPublisher.publishEvent(new NotificationEmailRequestedEvent(
                notification.getId(),
                notification.getEmail(),
                notification.getTitle(),
                notification.getDescription()
        ));

        return notificationMapper.toResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll(String authenticatedEmail) {
        return notificationRepository
                .findAllByEmailIgnoreCaseOrderByIdDesc(authenticatedEmail)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(UUID id, String authenticatedEmail) {
        return notificationMapper.toResponse(findById(id, authenticatedEmail));
    }

    @Transactional
    public void delete(UUID id, String authenticatedEmail) {
        notificationRepository.delete(findById(id, authenticatedEmail));
    }

    @Transactional
    public NotificationResponse readNotification(
            UUID id,
            String authenticatedEmail
    ) {
        Notification notification = findById(id, authenticatedEmail);
        notification.setStatusRead(true);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public NotificationResponse toggleReadStatus(
            UUID id,
            String authenticatedEmail
    ) {
        Notification notification = findById(id, authenticatedEmail);
        notification.setStatusRead(!notification.isStatusRead());
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public void markAllAsRead(String authenticatedEmail) {
        notificationRepository.markAllAsReadByEmail(authenticatedEmail);
    }

    @Transactional(readOnly = true)
    public long unreadCount(String authenticatedEmail) {
        return notificationRepository
                .countByEmailIgnoreCaseAndStatusReadFalse(authenticatedEmail);
    }

    private Notification findById(UUID id, String authenticatedEmail) {
        return notificationRepository
                .findByIdAndEmailIgnoreCase(id, authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação", id));
    }
}

package com.weg.Maintenance_API.notification.service;

import java.util.UUID;

import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.entity.Notification;
import com.weg.Maintenance_API.notification.event.NotificationEmailRequestedEvent;
import com.weg.Maintenance_API.notification.mapper.NotificationMapper;
import com.weg.Maintenance_API.notification.repository.NotificationRepository;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.preference.entity.NotificationPreference;
import com.weg.Maintenance_API.user.preference.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public NotificationResponse createSystemNotification(String email, String title, String about, String description) {
        Notification notification = notificationRepository.save(new Notification(email, title, about, description));
        eventPublisher.publishEvent(new NotificationEmailRequestedEvent(
                notification.getId(), notification.getEmail(), notification.getTitle(), notification.getDescription()));
        return notificationMapper.toResponse(notification);
    }

    /** Creates in-app and/or email notifications according to the recipient's preferences. */
    @Transactional
    public void notifyUser(User recipient, String title, String about, String description) {
        NotificationPreference preference = preferenceRepository.findByUserId(recipient.getId()).orElse(null);
        boolean inAppEnabled = preference == null || preference.isInAppEnabled();
        boolean emailEnabled = preference == null || preference.isEmailEnabled();
        UUID notificationId = null;
        if (inAppEnabled) {
            Notification notification = notificationRepository.save(
                    new Notification(recipient.getEmail(), title, about, description));
            notificationId = notification.getId();
        }
        if (emailEnabled) {
            eventPublisher.publishEvent(new NotificationEmailRequestedEvent(
                    notificationId, recipient.getEmail(), title, description));
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAll(String authenticatedEmail, Pageable pageable) {
        return notificationRepository.findAllByEmailIgnoreCaseOrderByIdDesc(authenticatedEmail, pageable)
                .map(notificationMapper::toResponse);
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
    public NotificationResponse readNotification(UUID id, String authenticatedEmail) {
        Notification notification = findById(id, authenticatedEmail);
        notification.setStatusRead(true);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public NotificationResponse toggleReadStatus(UUID id, String authenticatedEmail) {
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
        return notificationRepository.countByEmailIgnoreCaseAndStatusReadFalse(authenticatedEmail);
    }

    private Notification findById(UUID id, String authenticatedEmail) {
        return notificationRepository.findByIdAndEmailIgnoreCase(id, authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação", id));
    }
}
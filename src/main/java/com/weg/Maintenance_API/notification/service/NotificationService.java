package com.weg.Maintenance_API.notification.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
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
    // Cria uma notificacao apenas para fluxos internos do sistema.
    public NotificationResponse createSystemNotification(
            String email,
            String title,
            String about,
            String description
    ) {
        Notification notification = notificationRepository.save(
                new Notification(email, title, about, description)
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
    // Retorna somente as notificacoes do usuario autenticado de forma paginada.
    public Page<NotificationResponse> getAll(
            String authenticatedEmail,
            Pageable pageable
    ) {
        return notificationRepository
                .findAllByEmailIgnoreCaseOrderByIdDesc(authenticatedEmail, pageable)
                .map(notificationMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public NotificationResponse getById(UUID id, String authenticatedEmail) {
        return notificationMapper.toResponse(findById(id, authenticatedEmail));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id, String authenticatedEmail) {
        notificationRepository.delete(findById(id, authenticatedEmail));
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional
    public NotificationResponse readNotification(
            UUID id,
            String authenticatedEmail
    ) {
        Notification notification = findById(id, authenticatedEmail);
        notification.setStatusRead(true);
        return notificationMapper.toResponse(notification);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public NotificationResponse toggleReadStatus(
            UUID id,
            String authenticatedEmail
    ) {
        Notification notification = findById(id, authenticatedEmail);
        notification.setStatusRead(!notification.isStatusRead());
        return notificationMapper.toResponse(notification);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public void markAllAsRead(String authenticatedEmail) {
        notificationRepository.markAllAsReadByEmail(authenticatedEmail);
    }

    // Executa a operacao deste metodo.
    @Transactional(readOnly = true)
    public long unreadCount(String authenticatedEmail) {
        return notificationRepository
                .countByEmailIgnoreCaseAndStatusReadFalse(authenticatedEmail);
    }

    // Busca os dados necessarios para esta operacao.
    private Notification findById(UUID id, String authenticatedEmail) {
        return notificationRepository
                .findByIdAndEmailIgnoreCase(id, authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("NotificaÃ§Ã£o", id));
    }
}

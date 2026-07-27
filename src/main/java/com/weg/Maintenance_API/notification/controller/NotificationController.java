package com.weg.Maintenance_API.notification.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getAll(
            Authentication authentication,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                notificationService.getAll(authentication.getName(), pageable)
        );
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.getById(id, authentication.getName())
        );
    }

    // Remove ou invalida os dados solicitados.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        notificationService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Busca os dados necessarios para esta operacao.
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> readNotification(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.readNotification(id, authentication.getName())
        );
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> patchReadNotification(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return readNotification(id, authentication);
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}/toggle-read")
    public ResponseEntity<NotificationResponse> toggleReadStatus(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.toggleReadStatus(id, authentication.getName())
        );
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Executa a operacao deste metodo.
    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(Authentication authentication) {
        return ResponseEntity.ok(
                notificationService.unreadCount(authentication.getName())
        );
    }
}

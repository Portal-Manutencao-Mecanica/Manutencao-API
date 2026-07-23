package com.weg.Maintenance_API.notification.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.notification.dto.Request.NotificationRequest;
import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> create(
            @Valid @RequestBody NotificationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.getAll(authentication.getName())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.getById(id, authentication.getName())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        notificationService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> readNotification(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.readNotification(id, authentication.getName())
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> patchReadNotification(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return readNotification(id, authentication);
    }

    @PatchMapping("/{id}/toggle-read")
    public ResponseEntity<NotificationResponse> toggleReadStatus(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                notificationService.toggleReadStatus(id, authentication.getName())
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(Authentication authentication) {
        return ResponseEntity.ok(
                notificationService.unreadCount(authentication.getName())
        );
    }
}

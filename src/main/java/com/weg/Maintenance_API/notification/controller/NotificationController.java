package com.weg.Maintenance_API.notification.controller;

import com.weg.Maintenance_API.notification.dto.Request.NotificationRequest;
import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @Valid @RequestBody NotificationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> readNotification(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(notificationService.readNotification(id));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> patchReadNotification(
            @PathVariable Long id
    ) {
        return readNotification(id);
    }
}
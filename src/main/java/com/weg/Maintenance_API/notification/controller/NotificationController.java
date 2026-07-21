package com.weg.Maintenance_API.notification.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.notification.dto.Request.NotificationRequest;
import com.weg.Maintenance_API.notification.dto.Response.NotificationResponse;
import com.weg.Maintenance_API.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping()
    public ResponseEntity<NotificationResponse> postMethodName(@RequestBody NotificationRequest notificationRequest) {
        return new ResponseEntity<>(notificationService.create(notificationRequest), HttpStatus.CREATED);
    }
    

}

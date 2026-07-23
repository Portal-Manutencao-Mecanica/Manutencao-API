package com.weg.Maintenance_API.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weg.Maintenance_API.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}

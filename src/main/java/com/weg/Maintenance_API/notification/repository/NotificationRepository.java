package com.weg.Maintenance_API.notification.repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weg.Maintenance_API.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByEmailIgnoreCaseOrderByIdDesc(String email);

    Optional<Notification> findByIdAndEmailIgnoreCase(UUID id, String email);

    long countByEmailIgnoreCaseAndStatusReadFalse(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Notification notification
               set notification.statusRead = true
             where lower(notification.email) = lower(:email)
               and notification.statusRead = false
            """)
    int markAllAsReadByEmail(@Param("email") String email);
}

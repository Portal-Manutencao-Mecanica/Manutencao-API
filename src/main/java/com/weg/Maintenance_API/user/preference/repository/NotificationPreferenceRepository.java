package com.weg.Maintenance_API.user.preference.repository;

import com.weg.Maintenance_API.user.preference.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, UUID> {

    Optional<NotificationPreference> findByUserId(UUID userId);
}

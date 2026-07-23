package com.weg.Maintenance_API.coordinator.dto.response;

import java.time.LocalDateTime;

import com.weg.Maintenance_API.enums.Role;

public record CoordinatorResponseDto(
        Long id,
        String numberCard,
        String name,
        String email,
        Role role,
        boolean enabled,
        boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}


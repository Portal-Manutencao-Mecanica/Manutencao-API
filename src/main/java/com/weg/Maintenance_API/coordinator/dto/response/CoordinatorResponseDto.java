package com.weg.Maintenance_API.coordinator.dto.response;

import java.time.LocalDateTime;

import com.weg.Maintenance_API.enums.Role;

public record CoordinatorResponseDto(
        Long id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt) {
}



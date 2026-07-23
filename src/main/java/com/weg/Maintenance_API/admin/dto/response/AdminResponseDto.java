package com.weg.Maintenance_API.admin.dto.response;


import java.util.UUID;

import java.time.LocalDateTime;

import com.weg.Maintenance_API.enums.Role;

public record AdminResponseDto(
        UUID id,
        String numberCard,
        String name,
        String email,
        Role role,
        boolean enabled,
        boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

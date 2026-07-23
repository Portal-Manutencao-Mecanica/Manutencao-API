package com.weg.Maintenance_API.user.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.Role;

import java.time.LocalDateTime;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        Role role,
        String numberCard,
        boolean enabled,
        boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

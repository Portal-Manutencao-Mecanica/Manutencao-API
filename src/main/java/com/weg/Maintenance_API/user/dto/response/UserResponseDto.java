package com.weg.Maintenance_API.user.dto.response;

import com.weg.Maintenance_API.enums.Role;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
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

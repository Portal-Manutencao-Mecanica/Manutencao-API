package com.weg.Maintenance_API.admin.dto.response;

import java.time.LocalDateTime;

import com.weg.Maintenance_API.enums.Role;

public record AdminResponseDto(
        Long id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt) {
}


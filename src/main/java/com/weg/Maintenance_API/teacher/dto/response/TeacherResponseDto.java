package com.weg.Maintenance_API.teacher.dto.response;


import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Maintenance_API.enums.Role;

public record TeacherResponseDto(
        UUID id,
        String numberCard,
        String name,
        String email,
        Role role,
        List<UUID> classGroupIds,
        boolean enabled,
        boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}


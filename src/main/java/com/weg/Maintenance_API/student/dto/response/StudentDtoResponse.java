package com.weg.Maintenance_API.student.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Maintenance_API.enums.Role;

public record StudentDtoResponse(
        Long id,
        String name,
        String email,
        Role role,
        List<Long> classGroupIds,
        boolean enabled,
        boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}


package com.weg.Maintenance_API.teacher.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Maintenance_API.enums.Role;

public record TeacherResponseDto(
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


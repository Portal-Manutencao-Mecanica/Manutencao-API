package com.weg.Maintenance_API.teacher.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

public record TeacherResponseDto(
        Long id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt,
        List<ClassGroup> classGroups) {
}



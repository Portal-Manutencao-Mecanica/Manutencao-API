package com.weg.Maintenance_API.student.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

public record StudentDtoResponse(
        Long id,
        String name,
        String email,
        Role role,
        List<ClassGroup> classGroups,
        LocalDateTime createdAt) {
}



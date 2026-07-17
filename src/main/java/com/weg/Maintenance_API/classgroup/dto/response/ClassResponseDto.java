package com.weg.Maintenance_API.classgroup.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.teacher.entity.Teacher;

public record ClassResponseDto(
        Long id,
        String name,
        String email,
        String acronym,
        Role role,
        LocalDateTime createdAt,
        List<Teacher> teachers,
        List<Student> students) {
}



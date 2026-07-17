package com.weg.Maintenance_API.classgroup.dto.request;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ClassRequestDto(
        @NotBlank(message = "name can't be blank")
        String name,
        @NotBlank(message = "email can't be blank")
        @Email(message = "email must be valid")
        String email,
        @NotBlank(message = "acronym can't be blank")
        String acronym,
        @NotNull(message = "role can't be null")
        Role role,
        LocalDateTime createdAt,
        List<Teacher> teachers,
        List<Student> students) {
}



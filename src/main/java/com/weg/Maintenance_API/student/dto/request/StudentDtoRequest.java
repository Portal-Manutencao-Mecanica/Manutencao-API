package com.weg.Maintenance_API.student.dto.request;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record StudentDtoRequest(
        @NotBlank(message = "name can't be blank")
        String name,
        @NotBlank(message = "email can't be blank")
        @Email(message = "email must be valid")
        String email,
        List<ClassGroup> classGroups) {
}



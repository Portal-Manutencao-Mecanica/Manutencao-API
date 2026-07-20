package com.weg.Maintenance_API.teacher.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TeacherRequestDto(
        @NotBlank(message = "name can't be blank")
        @Size(min = 3, max = 120)
        String name,
        @NotBlank(message = "email can't be blank")
        @Email(message = "email must be valid")
        @Size(max = 150)
        String email,
        @NotBlank(message = "password can't be blank")
        @Size(min = 8, max = 72)
        String password,
        List<Long> classGroupIds
) {
}


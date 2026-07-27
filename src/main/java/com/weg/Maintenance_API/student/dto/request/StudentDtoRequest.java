package com.weg.Maintenance_API.student.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.validation.UniqueEmail;
import com.weg.Maintenance_API.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record StudentDtoRequest(
        @NotBlank(message = "name can't be blank")
        @Size(min = 3, max = 120)
        String name,
        @NotBlank(message = "email can't be blank")
        @Email(message = "email must be valid")
        @Size(max = 150)
        @UniqueEmail
        String email,
        @NotBlank(message = "password can't be blank")
        @ValidPassword
        String password,
        List<UUID> classGroupIds) {
}


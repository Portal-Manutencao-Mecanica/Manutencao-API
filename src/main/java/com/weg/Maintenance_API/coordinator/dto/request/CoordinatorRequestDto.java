package com.weg.Maintenance_API.coordinator.dto.request;

import com.weg.Maintenance_API.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CoordinatorRequestDto(
        @NotBlank(message = "name can't be blank")
        String name,
        @NotBlank(message = "email can't be blank")
        @Email(message = "email must be valid")
        String email,
        @NotNull(message = "role can't be null")
        Role role,
        LocalDateTime createdAt) {
}



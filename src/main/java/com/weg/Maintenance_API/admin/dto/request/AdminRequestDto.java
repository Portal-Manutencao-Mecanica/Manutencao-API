package com.weg.Maintenance_API.admin.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminRequestDto(
        @NotBlank(message = "name can't be blank")
        String name,
        @NotBlank(message = "email can't be blank")
        @Email(message = "email must be valid")
        String email) {
}


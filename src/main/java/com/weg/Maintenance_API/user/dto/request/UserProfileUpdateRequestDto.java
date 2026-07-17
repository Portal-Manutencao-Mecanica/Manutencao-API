package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequestDto(
        @NotBlank
        @Size(min = 3, max = 120)
        String name,

        @NotBlank
        @Email
        @Size(max = 150)
        String email
) {
}

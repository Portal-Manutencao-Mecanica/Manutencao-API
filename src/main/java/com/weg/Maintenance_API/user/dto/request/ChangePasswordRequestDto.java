package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 72)
        String newPassword,

        @NotBlank
        String passwordConfirmation
) {
}

package com.weg.Maintenance_API.user.dto.request;

import com.weg.Maintenance_API.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(
        @NotBlank
        String currentPassword,

        @NotBlank
        @ValidPassword
        String newPassword,

        @NotBlank
        String passwordConfirmation
) {
}

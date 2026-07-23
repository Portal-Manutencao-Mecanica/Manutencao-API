package com.weg.Maintenance_API.auth.password.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "O token é obrigatório.")
        @Size(max = 200)
        String token,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(max = 128)
        String newPassword,

        @NotBlank(message = "A confirmação da senha é obrigatória.")
        String passwordConfirmation
) {
}

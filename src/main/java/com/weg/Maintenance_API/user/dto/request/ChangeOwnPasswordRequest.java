package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeOwnPasswordRequest(
        @NotBlank(message = "A senha atual é obrigatória.")
        String currentPassword,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(max = 128, message = "A senha deve possuir no máximo 128 caracteres.")
        String newPassword,

        @NotBlank(message = "A confirmação da senha é obrigatória.")
        String passwordConfirmation
) {
}

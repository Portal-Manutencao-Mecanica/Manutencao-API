package com.weg.Maintenance_API.auth.firstaccess.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompleteFirstAccessRequest(
        @NotBlank(message = "O código de verificação é obrigatório.")
        @Pattern(regexp = "\\d{6}", message = "O código de verificação deve possuir 6 números.")
        String code,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(max = 128, message = "A senha deve possuir no máximo 128 caracteres.")
        String newPassword,

        @NotBlank(message = "A confirmação da senha é obrigatória.")
        String passwordConfirmation
) {
}

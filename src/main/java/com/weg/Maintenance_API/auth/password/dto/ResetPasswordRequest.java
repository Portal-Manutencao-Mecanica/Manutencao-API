package com.weg.Maintenance_API.auth.password.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Executa a operacao deste metodo.
public record ResetPasswordRequest(
        @NotBlank(message = "O token Ã© obrigatÃ³rio.")
        @Size(max = 200)
        String token,

        @NotBlank(message = "A nova senha Ã© obrigatÃ³ria.")
        @Size(max = 128)
        String newPassword,

        @NotBlank(message = "A confirmaÃ§Ã£o da senha Ã© obrigatÃ³ria.")
        String passwordConfirmation
) {
}

package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Atualiza o estado conforme os dados informados.
public record ChangeOwnPasswordRequest(
        @NotBlank(message = "A senha atual Ã© obrigatÃ³ria.")
        String currentPassword,

        @NotBlank(message = "A nova senha Ã© obrigatÃ³ria.")
        @Size(max = 128, message = "A senha deve possuir no mÃ¡ximo 128 caracteres.")
        String newPassword,

        @NotBlank(message = "A confirmaÃ§Ã£o da senha Ã© obrigatÃ³ria.")
        String passwordConfirmation
) {
}

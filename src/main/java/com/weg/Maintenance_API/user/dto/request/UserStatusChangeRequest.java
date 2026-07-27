package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Executa a operacao deste metodo.
public record UserStatusChangeRequest(
        @NotBlank(message = "O motivo Ã© obrigatÃ³rio.")
        @Size(max = 500, message = "O motivo deve possuir no mÃ¡ximo 500 caracteres.")
        String reason
) {
}

package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Atualiza o estado conforme os dados informados.
public record UpdateOwnProfileRequest(
        @NotBlank(message = "O nome Ã© obrigatÃ³rio.")
        @Size(max = 150, message = "O nome deve possuir no mÃ¡ximo 150 caracteres.")
        String name
) {
}

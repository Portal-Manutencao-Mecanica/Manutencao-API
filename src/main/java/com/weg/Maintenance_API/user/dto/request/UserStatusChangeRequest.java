package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserStatusChangeRequest(
        @NotBlank(message = "O motivo é obrigatório.")
        @Size(max = 500, message = "O motivo deve possuir no máximo 500 caracteres.")
        String reason
) {
}

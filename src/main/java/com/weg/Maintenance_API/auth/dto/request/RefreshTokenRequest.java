package com.weg.Maintenance_API.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

// Executa a operacao deste metodo.
public record RefreshTokenRequest(
        @NotBlank(message = "O refresh token Ã© obrigatÃ³rio.")
        String refreshToken
) {
}

package com.weg.Maintenance_API.auth.password.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Executa a operacao deste metodo.
public record ForgotPasswordRequest(
        @NotBlank(message = "O e-mail Ã© obrigatÃ³rio.")
        @Email(message = "O e-mail informado Ã© invÃ¡lido.")
        @Size(max = 150)
        String email
) {
}

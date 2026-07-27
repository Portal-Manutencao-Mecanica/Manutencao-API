package com.weg.Maintenance_API.user.dto.request;

import com.weg.Maintenance_API.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// Cria e persiste os dados da operacao.
public record CreateUserRequest(
        @NotBlank(message = "O nome Ã© obrigatÃ³rio.")
        @Size(max = 150, message = "O nome deve possuir no mÃ¡ximo 150 caracteres.")
        String name,

        @NotBlank(message = "O username Ã© obrigatÃ³rio.")
        @Size(min = 3, max = 50, message = "O username deve possuir entre 3 e 50 caracteres.")
        String username,

        @NotBlank(message = "O e-mail Ã© obrigatÃ³rio.")
        @Email(message = "O e-mail informado Ã© invÃ¡lido.")
        @Size(max = 150, message = "O e-mail deve possuir no mÃ¡ximo 150 caracteres.")
        String email,

        @NotNull(message = "A role Ã© obrigatÃ³ria.")
        Role role,

        UUID organizationId
) {
}

package com.weg.Maintenance_API.user.dto.request;

import com.weg.Maintenance_API.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateUserRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve possuir no máximo 150 caracteres.")
        String name,

        @NotBlank(message = "O username é obrigatório.")
        @Size(min = 3, max = 50, message = "O username deve possuir entre 3 e 50 caracteres.")
        String username,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        @Size(max = 150, message = "O e-mail deve possuir no máximo 150 caracteres.")
        String email,

        @NotNull(message = "A role é obrigatória.")
        Role role,

        UUID organizationId
) {
}

package com.weg.Maintenance_API.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateUserRequest(
        @NotBlank(message = "O nome e obrigatorio.")
        @Size(max = 150, message = "O nome deve possuir no maximo 150 caracteres.")
        String name,

        @NotBlank(message = "O e-mail e obrigatorio.")
        @Email(message = "O e-mail informado e invalido.")
        @Size(max = 150, message = "O e-mail deve possuir no maximo 150 caracteres.")
        String email,

        @NotBlank(message = "O numero do cracha e obrigatorio.")
        @Size(max = 100, message = "O numero do cracha deve possuir no maximo 100 caracteres.")
        String numberCard,

        UUID organizationId
) {
}

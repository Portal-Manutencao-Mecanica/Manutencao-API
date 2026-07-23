package com.weg.Maintenance_API.organization.dto;

import com.weg.Maintenance_API.organization.entity.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRequest(
        @NotBlank(message = "O nome da organização é obrigatório.")
        @Size(max = 150, message = "O nome deve possuir no máximo 150 caracteres.")
        String name,

        @NotNull(message = "O tipo da organização é obrigatório.")
        OrganizationType type,

        @NotBlank(message = "O domínio de e-mail é obrigatório.")
        @Size(max = 150, message = "O domínio deve possuir no máximo 150 caracteres.")
        @Pattern(
                regexp = "^(?!-)(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$",
                message = "O domínio de e-mail informado é inválido."
        )
        String emailDomain
) {
}

package com.weg.Maintenance_API.organization.dto;

import com.weg.Maintenance_API.enums.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Cria e persiste os dados da operacao.
public record CreateOrganizationRequest(
        @NotBlank(message = "O nome da organizaÃ§Ã£o Ã© obrigatÃ³rio.")
        @Size(max = 150, message = "O nome deve possuir no mÃ¡ximo 150 caracteres.")
        String name,

        @NotNull(message = "O tipo da organizacao e obrigatorio.")
        OrganizationType type,

        @NotBlank(message = "O domÃ­nio de e-mail Ã© obrigatÃ³rio.")
        @Size(max = 150, message = "O domÃ­nio deve possuir no mÃ¡ximo 150 caracteres.")
        @Pattern(
                regexp = "^(?!-)(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$",
                message = "O domÃ­nio 'de e-mail informado Ã© invÃ¡lido."
        )
        String emailDomain
) {
}

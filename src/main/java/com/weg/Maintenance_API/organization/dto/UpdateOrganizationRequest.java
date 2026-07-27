package com.weg.Maintenance_API.organization.dto;

import com.weg.Maintenance_API.enums.OrganizationType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Atualiza o estado conforme os dados informados.
public record UpdateOrganizationRequest(
        @Size(min = 2, max = 150, message = "O nome deve possuir entre 2 e 150 caracteres.")
        String name,

        OrganizationType type,

        @Size(max = 150, message = "O domÃ­nio deve possuir no mÃ¡ximo 150 caracteres.")
        @Pattern(
                regexp = "^(?!-)(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$",
                message = "O domÃ­nio de e-mail informado Ã© invÃ¡lido."
        )
        String emailDomain
) {
}

package com.weg.Maintenance_API.organization.dto;

import com.weg.Maintenance_API.organization.entity.OrganizationType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateOrganizationRequest(
        @Size(min = 2, max = 150, message = "O nome deve possuir entre 2 e 150 caracteres.")
        String name,

        OrganizationType type,

        @Size(max = 150, message = "O domínio deve possuir no máximo 150 caracteres.")
        @Pattern(
                regexp = "^(?!-)(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$",
                message = "O domínio de e-mail informado é inválido."
        )
        String emailDomain
) {
}

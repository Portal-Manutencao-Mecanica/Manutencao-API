package com.weg.Maintenance_API.organization.dto;

import java.util.UUID;

// Executa a operacao deste metodo.
public record OrganizationSummaryResponse(
        UUID id,
        String name
) {
}

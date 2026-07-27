package com.weg.Maintenance_API.organization.dto;

import java.util.UUID;

public record OrganizationSummaryResponse(
        UUID id,
        String name
) {
}

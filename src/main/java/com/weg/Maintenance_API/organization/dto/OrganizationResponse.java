package com.weg.Maintenance_API.organization.dto;

import com.weg.Maintenance_API.organization.entity.OrganizationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        OrganizationType type,
        String emailDomain,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

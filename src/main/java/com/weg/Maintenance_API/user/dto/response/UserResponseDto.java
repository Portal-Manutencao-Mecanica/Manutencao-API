package com.weg.Maintenance_API.user.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.user.entity.UserAccountStatus;

import java.time.LocalDateTime;

public record UserResponseDto(
        UUID id,
        String name,
        String username,
        String email,
        Role role,
        UserAccountStatus status,
        boolean passwordChangeRequired,
        OrganizationSummaryResponse organization,
        String numberCard,
        boolean enabled,
        boolean accountNonLocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

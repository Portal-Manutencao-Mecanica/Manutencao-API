package com.weg.Maintenance_API.user.dto.response;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.dto.OrganizationSummaryResponse;
import com.weg.Maintenance_API.user.entity.UserAccountStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String username,
        String email,
        Role role,
        UserAccountStatus status,
        boolean passwordChangeRequired,
        OrganizationSummaryResponse organization,
        ProfilePhotoResponse photo,
        NotificationPreferenceResponse preferences,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

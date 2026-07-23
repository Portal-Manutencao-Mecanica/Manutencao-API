package com.weg.Maintenance_API.userimport.dto;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.userimport.entity.UserImportItemStatus;

import java.util.UUID;

public record UserImportItemResponse(
        UUID id,
        int row,
        String name,
        String username,
        String email,
        Role role,
        String organization,
        UserImportItemStatus status,
        UUID createdUserId,
        String errorCode,
        String field,
        String message
) {
}

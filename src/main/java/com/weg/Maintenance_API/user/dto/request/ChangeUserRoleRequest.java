package com.weg.Maintenance_API.user.dto.request;

import com.weg.Maintenance_API.enums.Role;
import jakarta.validation.constraints.NotNull;

public record ChangeUserRoleRequest(
        @NotNull(message = "A role é obrigatória.")
        Role role
) {
}

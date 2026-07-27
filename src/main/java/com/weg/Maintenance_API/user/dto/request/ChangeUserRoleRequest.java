package com.weg.Maintenance_API.user.dto.request;

import com.weg.Maintenance_API.enums.Role;
import jakarta.validation.constraints.NotNull;

// Atualiza o estado conforme os dados informados.
public record ChangeUserRoleRequest(
        @NotNull(message = "A role Ã© obrigatÃ³ria.")
        Role role
) {
}

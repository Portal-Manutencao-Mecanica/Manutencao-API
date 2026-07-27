package com.weg.Maintenance_API.user.dto.request;

import com.weg.Maintenance_API.enums.Role;

// Executa a operacao deste metodo.
public record AdminUpdateUserRequestDto(
        Role role,
        Boolean enabled,
        Boolean accountNonLocked
) {
}

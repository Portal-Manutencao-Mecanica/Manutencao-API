package com.weg.Maintenance_API.auth.password.event;

import java.util.UUID;

// Executa a operacao deste metodo.
public record PasswordResetRequestedEvent(
        UUID userId,
        String name,
        String email,
        String rawToken
) {
}

package com.weg.Maintenance_API.user.event;

import java.util.UUID;

// Executa a operacao deste metodo.
public record TemporaryCredentialsReissuedEvent(
        UUID userId,
        String name,
        String email,
        String temporaryPassword
) {
}

package com.weg.Maintenance_API.user.event;

import java.util.UUID;

// Executa a operacao deste metodo.
public record UserCreatedEvent(
        UUID userId,
        String name,
        String email,
        String temporaryPassword
) {
}

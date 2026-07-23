package com.weg.Maintenance_API.user.event;

import java.util.UUID;

public record UserCreatedEvent(
        UUID userId,
        String name,
        String email,
        String temporaryPassword
) {
}

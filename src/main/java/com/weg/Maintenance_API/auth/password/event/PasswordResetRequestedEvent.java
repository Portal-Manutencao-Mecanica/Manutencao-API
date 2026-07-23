package com.weg.Maintenance_API.auth.password.event;

import java.util.UUID;

public record PasswordResetRequestedEvent(
        UUID userId,
        String name,
        String email,
        String rawToken
) {
}

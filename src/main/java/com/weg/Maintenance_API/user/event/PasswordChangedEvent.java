package com.weg.Maintenance_API.user.event;

import java.util.UUID;

public record PasswordChangedEvent(
        UUID userId,
        String name,
        String email
) {
}

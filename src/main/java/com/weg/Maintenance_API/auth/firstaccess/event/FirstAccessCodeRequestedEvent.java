package com.weg.Maintenance_API.auth.firstaccess.event;

import java.util.UUID;

public record FirstAccessCodeRequestedEvent(
        UUID userId,
        String name,
        String email,
        String code
) {
}

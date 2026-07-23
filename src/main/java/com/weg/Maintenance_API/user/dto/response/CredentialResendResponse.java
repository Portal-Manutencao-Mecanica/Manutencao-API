package com.weg.Maintenance_API.user.dto.response;

import java.util.UUID;

public record CredentialResendResponse(
        UUID userId,
        boolean credentialsSent,
        String emailStatus,
        String message
) {
}

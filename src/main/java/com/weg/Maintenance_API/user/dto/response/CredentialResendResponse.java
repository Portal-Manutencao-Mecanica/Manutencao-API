package com.weg.Maintenance_API.user.dto.response;

import java.util.UUID;

// Executa a operacao deste metodo.
public record CredentialResendResponse(
        UUID userId,
        boolean credentialsSent,
        String emailStatus,
        String message
) {
}

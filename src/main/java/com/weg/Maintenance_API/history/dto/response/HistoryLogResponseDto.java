package com.weg.Maintenance_API.history.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.HistoryAction;
import com.weg.Maintenance_API.enums.Role;

import java.time.LocalDateTime;

// Executa a operacao deste metodo.
public record HistoryLogResponseDto(
        UUID id,
        HistoryAction action,
        String entityType,
        UUID entityId,
        String description,
        Role actorRole,
        LocalDateTime createdAt,
        UUID actorId,
        String actorName
) {
}

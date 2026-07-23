package com.weg.Maintenance_API.history.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.HistoryAction;
import com.weg.Maintenance_API.enums.Role;

import java.time.LocalDateTime;

public record HistoryLogResponseDto(
        UUID id,
        String numberCard,
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

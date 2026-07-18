package com.weg.Maintenance_API.history.dto.response;

import com.weg.Maintenance_API.enums.HistoryAction;
import com.weg.Maintenance_API.enums.Role;

import java.time.LocalDateTime;

public record HistoryLogResponseDto(
        Long id,
        HistoryAction action,
        String entityType,
        Long entityId,
        String description,
        Role actorRole,
        LocalDateTime createdAt,
        Long actorId,
        String actorName
) {
}

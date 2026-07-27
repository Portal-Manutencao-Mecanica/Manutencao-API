package com.weg.Maintenance_API.userimport.dto;

import com.weg.Maintenance_API.enums.UserImportStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Executa a operacao deste metodo.
public record UserImportResponse(
        UUID importId,
        String filename,
        int totalRows,
        int created,
        int failed,
        UserImportStatus status,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        List<UserImportItemResponse> items
) {
}

package com.weg.Maintenance_API.history.dto.request;

import com.weg.Maintenance_API.enums.HistoryAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record HistoryLogCreateRequestDto(
        @NotNull
        HistoryAction action,

        @NotBlank
        @Size(max = 100)
        String entityType,

        @NotNull
        @Positive
        Long entityId,

        String description
) {
}

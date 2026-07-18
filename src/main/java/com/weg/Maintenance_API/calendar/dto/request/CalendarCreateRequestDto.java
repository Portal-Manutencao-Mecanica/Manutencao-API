package com.weg.Maintenance_API.calendar.dto.request;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CalendarCreateRequestDto(
        @NotBlank
        String scheduledAction,

        @NotNull
        TaskCriticality criticality,

        @NotNull
        @FutureOrPresent
        LocalDateTime scheduledFor,

        @NotNull
        @PastOrPresent
        LocalDateTime requestedAt,

        @Positive
        Long studentId,

        @NotNull
        @Positive
        Long teacherId,

        @NotNull
        @Positive
        Long equipmentId,

        @NotNull
        @Positive
        Integer machineId,

        @NotNull
        @Positive
        Long placeId,

        @NotNull
        MaintenanceType maintenanceType
) {
}

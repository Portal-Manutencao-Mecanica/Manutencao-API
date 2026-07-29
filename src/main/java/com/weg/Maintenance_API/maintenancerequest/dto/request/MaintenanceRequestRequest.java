package com.weg.Maintenance_API.maintenancerequest.dto.request;

import java.util.UUID;

import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Sector;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MaintenanceRequestRequest(
        @NotNull(message = "sector can't be null")
        @ValidEnum(message = "sector is invalid", enumClass = Sector.class)
        String sector,
        @NotNull(message = "priority can't be null")
        @ValidEnum(message = "priority is invalid", enumClass = Priority.class)
        String priority,
        @NotNull(message = "place can't be null")
        UUID placeId,
        @NotBlank(message = "description can't be blank")
        String description,
        @NotNull(message = "notified teacher can't be null")
        UUID notifiedTeacherId,
        @NotNull(message = "machine can't be null")
        UUID machineId
) {
}
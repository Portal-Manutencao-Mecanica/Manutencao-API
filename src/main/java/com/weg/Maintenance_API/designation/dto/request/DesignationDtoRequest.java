package com.weg.Maintenance_API.designation.dto.request;

import com.weg.Maintenance_API.enums.Sector;
import jakarta.validation.constraints.NotNull;

public record DesignationDtoRequest(
        @NotNull(message = "sector can't be null")
        Sector sector
) {
}


package com.weg.Maintenance_API.designation.dto.response;

import com.weg.Maintenance_API.enums.Sector;

public record DesignationDtoResponse(
        Long id,
        String numberCard,
        Sector sector
) {
}


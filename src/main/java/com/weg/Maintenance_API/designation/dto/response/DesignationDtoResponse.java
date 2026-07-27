package com.weg.Maintenance_API.designation.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.Sector;

public record DesignationDtoResponse(
        UUID id,
        String numberCard,
        Sector sector
) {
}


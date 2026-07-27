package com.weg.Maintenance_API.designation.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.Sector;

// Executa a operacao deste metodo.
public record DesignationDtoResponse(
        UUID id,
        Sector sector
) {
}

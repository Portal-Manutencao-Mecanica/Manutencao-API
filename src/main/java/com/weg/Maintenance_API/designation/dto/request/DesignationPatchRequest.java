package com.weg.Maintenance_API.designation.dto.request;

import com.weg.Maintenance_API.enums.Sector;

public record DesignationPatchRequest(
        Sector sector
) {
}
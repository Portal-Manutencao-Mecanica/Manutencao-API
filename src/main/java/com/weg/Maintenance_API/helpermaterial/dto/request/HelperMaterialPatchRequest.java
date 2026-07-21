package com.weg.Maintenance_API.helpermaterial.dto.request;

import com.weg.Maintenance_API.enums.HelperMaterialType;

public record HelperMaterialPatchRequest(
        String title,
        String description,
        String url,
        HelperMaterialType type
) {
}
package com.weg.Maintenance_API.helpermaterial.dto.response;

import com.weg.Maintenance_API.enums.HelperMaterialType;

public record HelperMaterialResponse(
        Long id,
        String title,
        String description,
        String url,
        HelperMaterialType type) {

}


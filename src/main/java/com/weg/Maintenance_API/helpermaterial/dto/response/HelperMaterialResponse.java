package com.weg.Maintenance_API.helpermaterial.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.HelperMaterialType;

public record HelperMaterialResponse(
        UUID id,
        String numberCard,
        String title,
        String description,
        String url,
        HelperMaterialType type) {

}


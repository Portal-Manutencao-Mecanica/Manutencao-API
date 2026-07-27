package com.weg.Maintenance_API.helpermaterial.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.HelperMaterialType;

// Executa a operacao deste metodo.
public record HelperMaterialResponse(
        UUID id,
        String title,
        String description,
        String url,
        HelperMaterialType type) {

}

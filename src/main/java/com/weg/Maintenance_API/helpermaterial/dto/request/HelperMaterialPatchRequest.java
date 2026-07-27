package com.weg.Maintenance_API.helpermaterial.dto.request;

import com.weg.Maintenance_API.enums.HelperMaterialType;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;

// Executa a operacao deste metodo.
public record HelperMaterialPatchRequest(
        String title,
        String description,
        String url,
        @ValidEnum(message = "O tipo de material informado e invalido.", enumClass = HelperMaterialType.class)
        String type
) {
}

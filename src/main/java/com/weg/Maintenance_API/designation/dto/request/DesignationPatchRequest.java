package com.weg.Maintenance_API.designation.dto.request;

import com.weg.Maintenance_API.enums.Sector;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;

// Executa a operacao deste metodo.
public record DesignationPatchRequest(
        @ValidEnum(message = "O setor informado e invalido.", enumClass = Sector.class)
        String sector
) {
}

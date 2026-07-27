package com.weg.Maintenance_API.machine.dto.request;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;

// Executa a operacao deste metodo.
public record MachinePatchRequest(
        String name,
        String patrimony,
        @ValidEnum(message = "A condicao informada e invalida.", enumClass = EquipmentCondition.class)
        String condition,
        String tag
) {
}

package com.weg.Maintenance_API.maintenancerequest.dto.request;

import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Sector;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;

// Executa a operacao deste metodo.
public record MaintenanceRequestPatchRequest(
        @ValidEnum(message = "O setor informado e invalido.", enumClass = Sector.class)
        String sector,
        @ValidEnum(message = "A prioridade informada e invalida.", enumClass = Priority.class)
        String priority,
        String description
) {
}

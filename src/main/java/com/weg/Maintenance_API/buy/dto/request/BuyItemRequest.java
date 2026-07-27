package com.weg.Maintenance_API.buy.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.equipment.entity.Equipment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Executa a operacao deste metodo.
public record BuyItemRequest(
        @NotNull(message = "equipment can't be null")
UUID equipmentId,
        @NotNull(message = "quantity can't be null")
        @Positive(message = "quantity must be greater than zero")
        Integer quantity,
        String technicalSpecification,
        String sap,
        String patrimony,
        String tag,
        String mechanicalSet
) {
}

package com.weg.Maintenance_API.buy.dto.request;

import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BuyItemRequest(
        @NotNull(message = "equipment can't be null")
        @EntityExists(entityClass = Equipment.class, message = "equipment not found")
        Long equipmentId,
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

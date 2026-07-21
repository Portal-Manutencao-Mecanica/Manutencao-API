package com.weg.Maintenance_API.equipment.dto.request;

import java.math.BigDecimal;

public record EquipmentPatchRequest(
        String name,
        String sap,
        BigDecimal unitPrice,
        Integer availableQuantity
) {
}
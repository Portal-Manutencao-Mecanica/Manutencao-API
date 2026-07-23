package com.weg.Maintenance_API.equipment.dto.response;

import java.math.BigDecimal;

public record EquipmentResponse(
        Long id,
        String numberCard,
        String name,
        String sap,
        BigDecimal unitPrice,
        Integer availableQuantity) {

}

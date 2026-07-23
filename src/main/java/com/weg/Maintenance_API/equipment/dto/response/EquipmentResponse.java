package com.weg.Maintenance_API.equipment.dto.response;


import java.util.UUID;

import java.math.BigDecimal;

public record EquipmentResponse(
        UUID id,
        String numberCard,
        String name,
        String sap,
        BigDecimal unitPrice,
        Integer availableQuantity) {

}

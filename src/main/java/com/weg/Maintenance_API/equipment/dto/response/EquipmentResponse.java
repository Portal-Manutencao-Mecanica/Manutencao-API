package com.weg.Maintenance_API.equipment.dto.response;

import java.math.BigDecimal;

public record EquipmentResponse(
        Long id,
        String name,
        String sap,
        BigDecimal price,
        int quantity) {

}



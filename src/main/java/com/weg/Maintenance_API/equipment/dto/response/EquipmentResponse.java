package com.weg.Maintenance_API.equipment.dto.response;


import java.util.UUID;

import java.math.BigDecimal;

// Executa a operacao deste metodo.
public record EquipmentResponse(
        UUID id,
        String name,
        String sap,
        String patrimony,
        String tag,
        BigDecimal unitPrice,
        Integer availableQuantity) {

}

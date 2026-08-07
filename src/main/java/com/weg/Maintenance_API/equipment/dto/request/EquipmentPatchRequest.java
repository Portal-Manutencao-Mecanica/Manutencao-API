package com.weg.Maintenance_API.equipment.dto.request;

import java.math.BigDecimal;

// Executa a operacao deste metodo.
public record EquipmentPatchRequest(
        String name,
        String sap,
        String patrimony,
        String tag,
        BigDecimal unitPrice,
        Integer availableQuantity,
        String image
) {
}

package com.weg.Manutencao_API.equipamento.dto.response;

import java.math.BigDecimal;

public record EquipmentResponse(
        Long id,
        String name,
        String sap,
        BigDecimal price,
        int quantity) {

}

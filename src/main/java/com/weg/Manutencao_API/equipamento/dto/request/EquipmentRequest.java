package com.weg.Manutencao_API.equipamento.dto.request;

import java.math.BigDecimal;

public record EquipmentRequest(
    String name,
    String sap,
    BigDecimal price,
    int quantity
) {

}

package com.weg.Maintenance_API.buy.dto.response;


import java.util.UUID;

// Executa a operacao deste metodo.
public record BuyItemResponse(
        UUID id,
        UUID equipmentId,
        String equipmentName,
        Integer quantity,
        String technicalSpecification,
        String sap,
        String patrimony,
        String tag,
        String mechanicalSet
) {
}

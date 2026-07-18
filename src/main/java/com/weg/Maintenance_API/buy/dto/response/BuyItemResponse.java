package com.weg.Maintenance_API.buy.dto.response;

public record BuyItemResponse(
        Long id,
        Long equipmentId,
        String equipmentName,
        Integer quantity,
        String technicalSpecification,
        String sap,
        String patrimony,
        String tag,
        String mechanicalSet
) {
}

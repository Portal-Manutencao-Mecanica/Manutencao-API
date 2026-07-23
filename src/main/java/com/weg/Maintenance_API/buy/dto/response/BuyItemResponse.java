package com.weg.Maintenance_API.buy.dto.response;


import java.util.UUID;

public record BuyItemResponse(
        UUID id,
        String numberCard,
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

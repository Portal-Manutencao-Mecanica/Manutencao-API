package com.weg.Manutencao_API.compras.dto.response;


import java.time.LocalDate;
import java.util.List;

public record BuyDtoResponse(
        Long id,
        String status,
        StudentDtoResponse student,
        TeachetDtoResponse teacher,
        String technicalSpecification,
        int quantity,
        String sap,
        String purchaseJustification,
        ClassGroupDtoResponse classGroup,
        String tag,
        String patrimony,
        EquipmentDtoResponse equipmentId,
        String mechanicalSet,
        LocalDate createdAt,
        List<String> media
) {
}

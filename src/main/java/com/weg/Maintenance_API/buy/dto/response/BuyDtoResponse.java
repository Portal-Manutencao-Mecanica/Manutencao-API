package com.weg.Maintenance_API.buy.dto.response;

import java.time.LocalDate;
import java.util.List;

public record BuyDtoResponse(
        Long id,
        String status,
        StudentDtoResponse student,
        TeacherDtoResponse teacher,
        String technicalSpecification,
        int quantity,
        String sap,
        String purchaseJustification,
        ClassGroupDtoResponse classGroup,
        String tag,
        String patrimony,
        EquipmentDtoResponse equipment,
        String mechanicalSet,
        LocalDate createdAt,
        List<String> mediaFiles
) {
}



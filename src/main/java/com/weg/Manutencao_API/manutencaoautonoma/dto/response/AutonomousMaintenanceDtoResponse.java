package com.weg.Manutencao_API.manutencaoautonoma.dto.response;

import com.weg.Manutencao_API.enums.EquipmentCondition;
import com.weg.Manutencao_API.enums.EquipmentSituation;

import java.time.LocalDateTime;

public record AutonomousMaintenanceDtoResponse(
    Integer id,
    EquipmentSituation equipmentSituation,
    LocalDateTime dateTime,
    Long equipmentId,
    String equipmentName,
    EquipmentCondition equipmentCondition,
    String identifiedNonconformities,
    String patrimony,
    String tag,
    Long teacherId,
    String teacherName,
    Long studentId,
    String studentName
) {
}

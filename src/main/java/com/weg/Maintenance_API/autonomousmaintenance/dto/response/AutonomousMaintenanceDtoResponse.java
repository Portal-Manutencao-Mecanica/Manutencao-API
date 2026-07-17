package com.weg.Maintenance_API.autonomousmaintenance.dto.response;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;

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



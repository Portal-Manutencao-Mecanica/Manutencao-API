package com.weg.Maintenance_API.autonomousmaintenance.dto.response;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;

import java.time.LocalDateTime;

public record AutonomousMaintenanceDtoResponse(
        Long id,
        String numberCard,
        EquipmentSituation equipmentSituation,
        LocalDateTime inspectedAt,
        Long inspectedMachineId,
        String inspectedMachineName,
        EquipmentCondition equipmentCondition,
        String identifiedNonconformities,
        Long responsibleTeacherId,
        String responsibleTeacherName,
        Long responsibleStudentId,
        String responsibleStudentName
) {
}

package com.weg.Maintenance_API.autonomousmaintenance.dto.response;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;

import java.time.LocalDateTime;

public record AutonomousMaintenanceDtoResponse(
        UUID id,
        String numberCard,
        EquipmentSituation equipmentSituation,
        LocalDateTime inspectedAt,
        UUID inspectedMachineId,
        String inspectedMachineName,
        EquipmentCondition equipmentCondition,
        String identifiedNonconformities,
        UUID responsibleTeacherId,
        String responsibleTeacherName,
        UUID responsibleStudentId,
        String responsibleStudentName
) {
}

package com.weg.Maintenance_API.autonomousmaintenance.dto.response;

import com.weg.Maintenance_API.enums.AutonomousMaintenanceStatus;
import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AutonomousMaintenanceDtoResponse(
        UUID id,
        EquipmentSituation equipmentSituation,
        LocalDateTime scheduledFor,
        LocalDateTime inspectedAt,
        UUID inspectedMachineId,
        String inspectedMachineName,
        EquipmentCondition equipmentCondition,
        String identifiedNonconformities,
        UUID responsibleTeacherId,
        String responsibleTeacherName,
        List<AutonomousMaintenanceStudentResponse> students,
        AutonomousMaintenanceStatus status,
        UUID coordinatorApproverId,
        String coordinatorApproverName,
        LocalDateTime approvedAt,
        String rejectionReason,
        UUID calendarEventId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

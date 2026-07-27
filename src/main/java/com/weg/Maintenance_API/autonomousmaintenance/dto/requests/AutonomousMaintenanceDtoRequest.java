package com.weg.Maintenance_API.autonomousmaintenance.dto.requests;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

// Executa a operacao deste metodo.
public record AutonomousMaintenanceDtoRequest(
        @NotNull(message = "Equipment situation can't be null")
        EquipmentSituation equipmentSituation,
        @NotNull(message = "Inspected at can't be null")
        @PastOrPresent(message = "Inspected at can't be future")
        LocalDateTime inspectedAt,
        @NotNull(message = "Machine can't be null")
UUID inspectedMachineId,
        @NotNull(message = "Equipment condition can't be null")
        @ValidEnum(message = "condition is invalid",enumClass = EquipmentCondition.class)
        String equipmentCondition,
        @NotBlank(message = "Identified non conformity can't be blank")
        String identifiedNonconformities,
        @NotNull(message = "Responsible teacher can't be null")
UUID responsibleTeacherId,
        @NotNull(message = "Responsible student can't be null")
UUID responsibleStudentId
) {
}

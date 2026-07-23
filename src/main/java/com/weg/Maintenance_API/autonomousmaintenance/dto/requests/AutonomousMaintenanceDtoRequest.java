package com.weg.Maintenance_API.autonomousmaintenance.dto.requests;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record AutonomousMaintenanceDtoRequest(
        @NotNull(message = "Equipment situation can't be null")
        EquipmentSituation equipmentSituation,
        @NotNull(message = "Inspected at can't be null")
        @PastOrPresent(message = "Inspected at can't be future")
        LocalDateTime inspectedAt,
        @NotNull(message = "Machine can't be null")
        @EntityExists(entityClass = Machine.class, message = "machine not found")
        UUID inspectedMachineId,
        @NotNull(message = "Equipment condition can't be null")
        EquipmentCondition equipmentCondition,
        @NotBlank(message = "Identified non conformity can't be blank")
        String identifiedNonconformities,
        @NotNull(message = "Responsible teacher can't be null")
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        UUID responsibleTeacherId,
        @NotNull(message = "Responsible student can't be null")
        @EntityExists(entityClass = Student.class, message = "student not found")
        UUID responsibleStudentId
) {
}

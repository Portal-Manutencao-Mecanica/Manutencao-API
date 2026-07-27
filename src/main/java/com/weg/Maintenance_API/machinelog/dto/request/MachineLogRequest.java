package com.weg.Maintenance_API.machinelog.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

// Executa a operacao deste metodo.
public record MachineLogRequest(
        String title,
        String description,
        String executionReport,
        @NotNull(message = "task situation can't be null")
        @ValidEnum(message = "task situation is invalid",enumClass = TaskSituation.class)
        String taskSituation,
        @NotNull(message = "machine can't be null")
UUID machineId,
        String servicePerformed,
UUID responsibleTeacherId,
        LocalDateTime teacherConcludedAt,
        LocalDateTime executionStartedAt,
        LocalDateTime executionEndedAt,
        String plannedAction,
        @NotNull(message = "task criticality can't be null")
        @ValidEnum(message = "task criticality is invalid",enumClass = TaskCriticality.class)
        String taskCriticality,
UUID placeId,
        @ValidEnum(message = "maintenance type is invalid",enumClass = MaintenanceType.class)
        String maintenanceType,
UUID classGroupId,
List<UUID> assignedStudentIds,
        String reportLink
) {
}

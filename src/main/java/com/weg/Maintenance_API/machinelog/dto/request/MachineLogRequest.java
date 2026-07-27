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
import com.weg.Maintenance_API.validation.AllEntitiesExist;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record MachineLogRequest(
        String title,
        String description,
        String executionReport,
        @NotNull(message = "task situation can't be null")
        TaskSituation taskSituation,
        @NotNull(message = "machine can't be null")
        @EntityExists(entityClass = Machine.class, message = "machine not found")
        UUID machineId,
        String servicePerformed,
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        UUID responsibleTeacherId,
        LocalDateTime teacherConcludedAt,
        LocalDateTime executionStartedAt,
        LocalDateTime executionEndedAt,
        String plannedAction,
        @NotNull(message = "task criticality can't be null")
        TaskCriticality taskCriticality,
        @EntityExists(entityClass = Place.class, message = "place not found")
        UUID placeId,
        MaintenanceType maintenanceType,
        @EntityExists(entityClass = ClassGroup.class, message = "class group not found")
        UUID classGroupId,
        @AllEntitiesExist(entityClass = Student.class, message = "student not found")
        List<UUID> assignedStudentIds,
        String reportLink
) {
}

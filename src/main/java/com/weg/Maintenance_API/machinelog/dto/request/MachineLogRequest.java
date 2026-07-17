package com.weg.Maintenance_API.machinelog.dto.request;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;
import java.util.List;

public record MachineLogRequest(
        String title,
        String description,
        String content,
        @NotNull(message = "task situation can't be null")
        TaskSituation taskSituation,
        @NotNull(message = "machine can't be null")
        @EntityExists(entityClass = Machine.class, message = "machine not found")
        Integer machineId,
        String serviceExecute,
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        Long teacherId,
        @NotNull(message = "scheduled date can't be null")
        @FutureOrPresent(message = "scheduled date can't be in the past")
        LocalDateTime scheduledFor,
        @NotNull(message = "requested date can't be null")
        @PastOrPresent(message = "requested date can't be in the future")
        LocalDateTime requestedAt,
        String actionToExecute,
        @NotNull(message = "task criticality can't be null")
        TaskCriticality taskCriticality,
        byte[] image,
        @EntityExists(entityClass = Place.class, message = "place not found")
        Long placeId,
        MaintenanceType maintenanceType,
        String registrationPeriod,
        @EntityExists(entityClass = ClassGroup.class, message = "class group not found")
        Long classGroupId,
        @EntityExists(entityClass = Student.class, message = "student not found")
        List<Long> assignedStudentIds,
        String reportLink) {
}


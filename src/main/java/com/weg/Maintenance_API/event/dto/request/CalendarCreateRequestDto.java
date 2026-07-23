package com.weg.Maintenance_API.event.dto.request;


import java.util.UUID;

import com.weg.Maintenance_API.enums.MaintenanceType;
import com.weg.Maintenance_API.enums.TaskCriticality;
import com.weg.Maintenance_API.enums.TaskSituation;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record CalendarCreateRequestDto(
        @NotBlank(message = "scheduled action can't be blank")
        String scheduledAction,
        @NotNull(message = "criticality can't be null")
        TaskCriticality criticality,
        @NotNull(message = "scheduled for can't be null")
        @FutureOrPresent(message = "scheduled for can't be in the past")
        LocalDateTime scheduledFor,
        @NotNull(message = "requested at can't be null")
        @PastOrPresent(message = "requested at can't be in the future")
        LocalDateTime requestedAt,
        @EntityExists(entityClass = Student.class, message = "student not found")
        UUID studentId,
        @NotNull(message = "teacher can't be null")
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        UUID teacherId,
        @NotNull(message = "equipment can't be null")
        @EntityExists(entityClass = Equipment.class, message = "equipment not found")
        UUID equipmentId,
        @NotNull(message = "machine can't be null")
        @EntityExists(entityClass = Machine.class, message = "machine not found")
        UUID machineId,
        @NotNull(message = "place can't be null")
        @EntityExists(entityClass = Place.class, message = "place not found")
        UUID placeId,
        @NotNull(message = "maintenance type can't be null")
        MaintenanceType maintenanceType,
        TaskSituation status
) {
}
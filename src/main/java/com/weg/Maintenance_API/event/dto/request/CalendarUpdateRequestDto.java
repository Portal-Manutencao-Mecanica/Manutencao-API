package com.weg.Maintenance_API.event.dto.request;

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
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record CalendarUpdateRequestDto(
        String scheduledAction,
        TaskCriticality criticality,
        @FutureOrPresent(message = "scheduled for can't be in the past") LocalDateTime scheduledFor,
        @PastOrPresent(message = "requested at can't be in the future") LocalDateTime requestedAt,
        @EntityExists(entityClass = Student.class, message = "student not found") Long studentId,
        @EntityExists(entityClass = Teacher.class, message = "teacher not found") Long teacherId,
        @EntityExists(entityClass = Equipment.class, message = "equipment not found") Long equipmentId,
        @EntityExists(entityClass = Machine.class, message = "machine not found") Long machineId,
        @EntityExists(entityClass = Place.class, message = "place not found") Long placeId,
        MaintenanceType maintenanceType,
        TaskSituation status
) {
}
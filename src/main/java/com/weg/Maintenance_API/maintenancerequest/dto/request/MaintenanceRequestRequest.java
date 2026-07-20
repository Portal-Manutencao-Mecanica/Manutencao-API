package com.weg.Maintenance_API.maintenancerequest.dto.request;

import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.enums.Sector;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.AllEntitiesExist;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MaintenanceRequestRequest(
        @NotNull(message = "sector can't be null")
        Sector sector,
        @NotNull(message = "priority can't be null")
        Priority priority,
        @NotEmpty(message = "assigned students can't be empty")
        @AllEntitiesExist(entityClass = Student.class, message = "student not found")
        List<Long> assignedStudentIds,
        @NotNull(message = "place can't be null")
        @EntityExists(entityClass = Place.class, message = "place not found")
        Long placeId,
        @NotBlank(message = "description can't be blank")
        String description,
        @NotNull(message = "notified teacher can't be null")
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        Long notifiedTeacherId,
        @NotNull(message = "machine can't be null")
        @EntityExists(entityClass = Machine.class, message = "machine not found")
        Long machineId
) {
}

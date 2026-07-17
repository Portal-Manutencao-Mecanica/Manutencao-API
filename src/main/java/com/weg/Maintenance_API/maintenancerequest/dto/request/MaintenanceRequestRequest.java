package com.weg.Maintenance_API.maintenancerequest.dto.request;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.designation.entity.Designation;
import com.weg.Maintenance_API.enums.Priority;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MaintenanceRequestRequest(
    @NotNull(message = "designation can't be null")
    @EntityExists(entityClass = Designation.class, message = "designation not found")
    Long designationId,
    @NotNull(message = "priority can't be null")
    Priority priority,
    @NotEmpty(message = "students can't be empty")
    @EntityExists(entityClass = Student.class, message = "student not found")
    // TODO SECURITY: obtain authenticated student from JWT instead of request body.
    List<Long> studentIds,
    @NotNull(message = "place can't be null")
    @EntityExists(entityClass = Place.class, message = "place not found")
    Long placeId,
    @NotBlank(message = "description can't be blank")
    String description,
    @NotEmpty(message = "anomaly images can't be empty")
    List<byte[]> anomalyImages,
    @NotBlank(message = "patrimony can't be blank")
    String patrimony,
    String tag,
    @NotNull(message = "teacher can't be null")
    @EntityExists(entityClass = Teacher.class, message = "teacher not found")
    Long teacherId,
    @NotNull(message = "machine can't be null")
    @EntityExists(entityClass = Machine.class, message = "machine not found")
    Integer machineId
) {
}


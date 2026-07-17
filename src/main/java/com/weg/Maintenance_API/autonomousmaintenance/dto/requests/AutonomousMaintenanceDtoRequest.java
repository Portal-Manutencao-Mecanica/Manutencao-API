package com.weg.Maintenance_API.autonomousmaintenance.dto.requests;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

public record AutonomousMaintenanceDtoRequest(
    @NotNull(message = "Equipment situation can't be null")
    EquipmentSituation equipmentSituation,
    @NotNull(message = "Date time can't be null")
    @PastOrPresent(message = "Date time can't be future")
    LocalDateTime dateTime,
    @NotNull(message = "Equipment can't be null")
    @EntityExists(entityClass = Equipment.class, message = "equipment not found")
    Long equipmentId,
    @NotNull(message = "Equipment condition can't be null")
    EquipmentCondition equipmentCondition,
    @NotNull(message = "Identified non conformity can't be null")
    String identifiedNonconformities,
    @NotNull(message = "Patrimony can't be null")
    String patrimony,
    String tag,
    @NotNull(message = "Teacher can't be null")
    @EntityExists(entityClass = Teacher.class, message = "teacher not found")
    Long teacherId,
    @NotNull(message = "Student can't be null")
    @EntityExists(entityClass = Student.class, message = "student not found")
    Long studentId
) {
}



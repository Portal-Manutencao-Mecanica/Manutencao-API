package com.weg.Maintenance_API.buy.dto.request;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record BuyDtoRequest(
        @NotNull(message = "student can't be null")
        @EntityExists(entityClass = Student.class, message = "student not found")
        // TODO SECURITY: obtain authenticated student from JWT instead of request body.
        Long studentId,
        @NotNull(message = "teacher can't be null")
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        Long teacherId,
        String technicalSpecification,
        @NotNull(message = "quantity can't be null")
        @Positive(message = "quantity must be greater than zero")
        Integer quantity,
        String sap,
        @NotBlank(message = "purchaseJustification can't be blank")
        String purchaseJustification,
        @NotNull(message = "classGroup can't be null")
        @EntityExists(entityClass = ClassGroup.class, message = "class group not found")
        Long classGroupId,
        String tag,
        @NotBlank(message = "patrimony can't be blank")
        String patrimony,
        @NotNull(message = "equipment can't be null")
        @EntityExists(entityClass = Equipment.class, message = "equipment not found")
        Long equipmentId,
        @NotBlank(message = "mechanicalSet can't be blank")
        String mechanicalSet,
        List<String> media
        ) {
}


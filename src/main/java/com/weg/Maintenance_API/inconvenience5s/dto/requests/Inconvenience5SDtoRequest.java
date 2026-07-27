package com.weg.Maintenance_API.inconvenience5s.dto.requests;


import java.util.UUID;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.enums.RegistrationPeriod;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.validation.AllEntitiesExist;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Inconvenience5SDtoRequest(
        @NotBlank(message = "inconvenience can't be blank")
        String inconvenience,
        @NotNull(message = "place can't be null")
        @EntityExists(entityClass = Place.class, message = "place not found")
        UUID placeId,
        @NotNull(message = "notified teacher can't be null")
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        UUID notifiedTeacherId,
        @NotNull(message = "class group can't be null")
        @EntityExists(entityClass = ClassGroup.class, message = "class group not found")
        UUID classGroupId,
        @NotEmpty(message = "involved students can't be empty")
        @AllEntitiesExist(entityClass = Student.class, message = "student not found")
        List<UUID> involvedStudentIds,
        String description,
        @NotNull(message = "registration period can't be null")
        RegistrationPeriod registrationPeriod
) {
}

package com.weg.Maintenance_API.inconvenience5s.dto.requests;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public record Inconvenience5SDtoRequest(
        @NotBlank(message = "inconvenience can't be blank")
        String inconvenience,
        @NotNull(message = "place can't be null")
        @EntityExists(entityClass = Place.class, message = "place not found")
        Long placeId,
        @NotNull(message = "teacher can't be null")
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        Long teacherId,
        @NotNull(message = "event date can't be null")
        @PastOrPresent(message = "event date can't be future")
        LocalDate dateTime,
        @NotNull(message = "class group can't be null")
        @EntityExists(entityClass = ClassGroup.class, message = "class group not found")
        Long classGroupId,
        @NotEmpty(message = "students can't be empty")
        @EntityExists(entityClass = Student.class, message = "student not found")
        // TODO SECURITY: obtain authenticated student from JWT instead of request body.
        List<Long> studentIds,
        String description,
        List<String> images,
        String registeredOccasion
) {
}


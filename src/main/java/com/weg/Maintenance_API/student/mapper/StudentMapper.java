package com.weg.Maintenance_API.student.mapper;


import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.student.dto.request.StudentDtoRequest;
import com.weg.Maintenance_API.student.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "classGroups", ignore = true)
    // Relationship must be resolved in the service layer.
    Student toEntity(StudentDtoRequest studentDtoRequest);

    @Mapping(target = "classGroupIds", source = "classGroups")
    StudentDtoResponse toResponse(Student student);

    default UUID mapClassGroupId(ClassGroup classGroup) {
        return classGroup == null ? null : classGroup.getId();
    }
}


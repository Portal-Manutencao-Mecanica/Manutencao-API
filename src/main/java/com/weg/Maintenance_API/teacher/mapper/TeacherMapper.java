package com.weg.Maintenance_API.teacher.mapper;


import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.teacher.dto.request.TeacherRequestDto;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "classGroups", ignore = true)
    // Relationship must be resolved in the service layer.
    Teacher toEntity(TeacherRequestDto teacherRequestDto);

    @Mapping(target = "classGroupIds", source = "classGroups")
    TeacherResponseDto toResponse(Teacher teacher);

    default UUID mapClassGroupId(ClassGroup classGroup) {
        return classGroup == null ? null : classGroup.getId();
    }
}


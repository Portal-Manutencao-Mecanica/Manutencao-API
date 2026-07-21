package com.weg.Maintenance_API.classgroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassGroupMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "students", ignore = true)
    // Relationships must be resolved in the service layer.
    ClassGroup toEntity(ClassRequestDto classRequestDto);

    default ClassResponseDto toResponse(ClassGroup classGroup) {
        if (classGroup == null) {
            return null;
        }

        List<Long> teacherIds = classGroup.getTeachers().stream()
                .map(teacher -> teacher.getId())
                .toList();
        List<Long> studentIds = classGroup.getStudents().stream()
                .map(student -> student.getId())
                .toList();

        return new ClassResponseDto(classGroup.getId(), classGroup.getAcronym(), teacherIds, studentIds);
    }
}


package com.weg.Maintenance_API.teacher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.teacher.dto.request.TeacherRequestDto;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;
import com.weg.Maintenance_API.teacher.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    
    @Mapping(target = "id", ignore = true)
    Teacher toEntity(TeacherRequestDto teacherRequestDto);

    TeacherResponseDto toResponse(Teacher teacher);
}



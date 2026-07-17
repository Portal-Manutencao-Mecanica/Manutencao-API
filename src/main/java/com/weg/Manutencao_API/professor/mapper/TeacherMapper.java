package com.weg.Manutencao_API.professor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.professor.dto.request.TeacherRequestDto;
import com.weg.Manutencao_API.professor.dto.response.TeacherResponseDto;
import com.weg.Manutencao_API.professor.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    
    @Mapping(target = "id", ignore = true)
    Teacher toEntity(TeacherRequestDto teacherRequestDto);

    TeacherResponseDto toResponse(Teacher teacher);
}

package com.weg.Manutencao_API.aluno.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.aluno.dto.request.StudentDtoRequest;
import com.weg.Manutencao_API.aluno.dto.response.StudentDtoResponse;
import com.weg.Manutencao_API.aluno.entity.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    Student toEntity(StudentDtoRequest studentDtoRequest);

    StudentDtoResponse toResponse(Student student);
    
}

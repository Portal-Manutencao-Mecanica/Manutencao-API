package com.weg.Manutencao_API.turma.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.turma.dto.request.ClassRequestDto;
import com.weg.Manutencao_API.turma.dto.response.ClassResponseDto;
import com.weg.Manutencao_API.turma.entity.ClassGroup;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    
    @Mapping(target = "id", ignore = true)
    ClassGroup toEntity(ClassRequestDto classRequestDto);

    ClassResponseDto toResponse(ClassGroup classGroup);
}

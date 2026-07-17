package com.weg.Manutencao_API.coordenador.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.coordenador.dto.request.CoordinatorRequestDto;
import com.weg.Manutencao_API.coordenador.dto.response.CoordinatorResponseDto;
import com.weg.Manutencao_API.coordenador.entity.Coordinator;

@Mapper
public interface CoordinatorMapper {

    @Mapping(target = "id", ignore = true)
    Coordinator toEntity(CoordinatorRequestDto coordinatorRequestDto);

    CoordinatorResponseDto toResponse(Coordinator coordinator);
}

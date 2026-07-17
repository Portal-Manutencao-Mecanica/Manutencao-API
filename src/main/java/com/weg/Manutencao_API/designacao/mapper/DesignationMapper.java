package com.weg.Manutencao_API.designacao.mapper;

import com.weg.Manutencao_API.designacao.dto.request.DesignationDtoRequest;
import com.weg.Manutencao_API.designacao.dto.response.DesignationDtoResponse;
import com.weg.Manutencao_API.designacao.entity.Designation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DesignationMapper {

    @Mapping(target = "id", ignore = true)
    Designation toEntity(DesignationDtoRequest request);


    DesignationDtoResponse toResponse(Designation designation);
}
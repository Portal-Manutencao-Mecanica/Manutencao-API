package com.weg.Manutencao_API.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.weg.Manutencao_API.local.dto.request.PlaceRequest;
import com.weg.Manutencao_API.local.dto.response.PlaceResponse;
import com.weg.Manutencao_API.local.entity.Place;

@Mapper(componentModel = "spring", uses = MachineMapper.class)
public interface PlaceMapper {

    @Mapping(target = "id", ignore = true)
    Place toEntity(PlaceRequest request);

    PlaceResponse toResponse(Place place);
}

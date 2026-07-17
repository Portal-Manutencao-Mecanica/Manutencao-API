package com.weg.Manutencao_API.local.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.weg.Manutencao_API.local.dto.PlaceRequest;
import com.weg.Manutencao_API.local.dto.PlaceResponse;
import com.weg.Manutencao_API.local.entity.Place;

@Mapper(componentModel = "spring", uses = MachineMapper.class)
public interface PlaceMapper {

    Place toEntity(PlaceRequest request);

    PlaceResponse toResponse(Place place);
}
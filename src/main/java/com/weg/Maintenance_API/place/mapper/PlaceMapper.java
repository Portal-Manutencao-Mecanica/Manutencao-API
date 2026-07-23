package com.weg.Maintenance_API.place.mapper;

import com.weg.Maintenance_API.place.dto.request.PlaceRequest;
import com.weg.Maintenance_API.place.dto.response.PlaceResponse;
import com.weg.Maintenance_API.place.entity.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "machines", ignore = true)
    Place toEntity(PlaceRequest request);

    PlaceResponse toResponse(Place place);
}



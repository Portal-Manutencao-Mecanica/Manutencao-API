package com.weg.Manutencao_API.materialapoio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.materialapoio.dto.request.HelperMaterialRequest;
import com.weg.Manutencao_API.materialapoio.dto.response.HelperMaterialResponse;
import com.weg.Manutencao_API.materialapoio.entity.HelperMaterial;

@Mapper(componentModel = "spring")
public interface HelperMaterialMapper {

    @Mapping(target = "id", ignore = true)
    HelperMaterial toEntity(HelperMaterialRequest helperMaterialRequest);

    HelperMaterialResponse toResponse(HelperMaterial helperMaterial);

}

/*
 * 
 * @Mapper(componentModel = "spring", uses = MachineMapper.class)
 * public interface PlaceMapper {
 * 
 * @Mapping(target = "id", ignore = true)
 * Place toEntity(PlaceRequest request);
 * 
 * PlaceResponse toResponse(Place place);
 * }
 * 
 * 
 */
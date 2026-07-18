package com.weg.Maintenance_API.helpermaterial.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialRequest;
import com.weg.Maintenance_API.helpermaterial.dto.response.HelperMaterialResponse;
import com.weg.Maintenance_API.helpermaterial.entity.HelperMaterial;

@Mapper(componentModel = "spring")
public interface HelperMaterialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "type", source = "type")
    HelperMaterial toEntity(HelperMaterialRequest helperMaterialRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "type", source = "type")
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

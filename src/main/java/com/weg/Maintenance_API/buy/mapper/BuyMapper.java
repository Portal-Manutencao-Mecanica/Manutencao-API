package com.weg.Maintenance_API.buy.mapper;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import com.weg.Maintenance_API.buy.entity.Buy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    @Mapping(target = "mediaFiles", source = "media")
    @Mapping(target = "createdAt", ignore = true)
    // Relationships must be resolved in the service layer.
    Buy toEntity(BuyDtoRequest request);

    BuyDtoResponse toResponse(Buy buy);
}



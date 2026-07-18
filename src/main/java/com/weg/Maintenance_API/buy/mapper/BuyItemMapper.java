package com.weg.Maintenance_API.buy.mapper;

import com.weg.Maintenance_API.buy.dto.request.BuyItemRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyItemResponse;
import com.weg.Maintenance_API.buy.entity.BuyItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuyItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buy", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    BuyItem toEntity(BuyItemRequest request);

    @Mapping(target = "equipmentId", source = "equipment.id")
    @Mapping(target = "equipmentName", source = "equipment.name")
    BuyItemResponse toResponse(BuyItem item);
}

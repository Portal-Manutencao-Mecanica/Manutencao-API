package com.weg.Maintenance_API.equipment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.equipment.dto.request.EquipmentRequest;
import com.weg.Maintenance_API.equipment.dto.response.EquipmentResponse;
import com.weg.Maintenance_API.equipment.entity.Equipment;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    @Mapping(target = "id", ignore = true)
    Equipment toEntity(EquipmentRequest equipmentRequest);

    EquipmentResponse toResponse(Equipment equipment);

}



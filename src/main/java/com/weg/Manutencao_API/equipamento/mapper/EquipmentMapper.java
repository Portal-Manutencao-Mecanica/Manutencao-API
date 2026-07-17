package com.weg.Manutencao_API.equipamento.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.equipamento.dto.request.EquipmentRequest;
import com.weg.Manutencao_API.equipamento.dto.response.EquipmentResponse;
import com.weg.Manutencao_API.equipamento.entity.Equipment;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    @Mapping(target = "id", ignore = true)
    Equipment toEntity(EquipmentRequest equipmentRequest);

    EquipmentResponse toResponse(Equipment equipment);

}

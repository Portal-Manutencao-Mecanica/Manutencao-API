package com.weg.Manutencao_API.maquina.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.local.mapper.PlaceMapper;
import com.weg.Manutencao_API.maquina.dto.request.MachineRequest;
import com.weg.Manutencao_API.maquina.dto.response.MachineResponse;
import com.weg.Manutencao_API.maquina.entity.Machine;

@Mapper(componentModel = "spring", uses = PlaceMapper.class, MachineLogMapper.class)
public interface MachineMapper {

    @Mapping(target = "id", ignore = true)
    Machine toEntity(MachineRequest machineRequest);

    MachineResponse toResponse(Machine machine);

}

package com.weg.Manutencao_API.livrolog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.livrolog.dto.request.MachineLogRequest;
import com.weg.Manutencao_API.livrolog.dto.response.MachineLogResponse;
import com.weg.Manutencao_API.livrolog.entity.MachineLog;
import com.weg.Manutencao_API.maquina.entity.Machine;
import com.weg.Manutencao_API.maquina.mapper.MachineMapper;

@Mapper(componentModel = "spring", uses = MachineMapper.class, TeacheMapper.class, PlaceMapper.class, StudentsMapper.class)
public interface MachineLogMapper {

    @Mapping(target = "id", ignore = true)
    MachineLog toEntity(MachineLogRequest machineLogRequest);

    MachineLogResponse toResponse(MachineLog machineLog);

}

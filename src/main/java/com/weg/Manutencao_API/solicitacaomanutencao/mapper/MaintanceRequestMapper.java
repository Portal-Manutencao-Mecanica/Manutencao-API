package com.weg.Manutencao_API.solicitacaomanutencao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.solicitacaomanutencao.dto.request.MaintanceRequestRequest;
import com.weg.Manutencao_API.solicitacaomanutencao.dto.response.MaintanceRequestResponse;
import com.weg.Manutencao_API.solicitacaomanutencao.entity.MaintenanceRequest;

@Mapper(componentModel = "spring", uses = DesignationMapper.class, StudentsMapper.class, 
    PlaceMapper.class, TeacheMapper.class, MachineMapper.class)
public interface MaintanceRequestMapper {

    @Mapping(target = "id", ignore = true)
    MaintenanceRequest toEntity(MaintanceRequestRequest maintanceRequestRequest);

    MaintanceRequestResponse toResponse(MaintenanceRequest maintenanceRequest);

}

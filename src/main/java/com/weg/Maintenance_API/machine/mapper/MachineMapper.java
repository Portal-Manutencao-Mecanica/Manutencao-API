package com.weg.Maintenance_API.machine.mapper;

import com.weg.Maintenance_API.machine.dto.request.MachineRequest;
import com.weg.Maintenance_API.machine.dto.response.MachineResponse;
import com.weg.Maintenance_API.machine.entity.Machine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MachineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "machineLogs", ignore = true)
    // Relationship must be resolved in the service layer.
    Machine toEntity(MachineRequest machineRequest);

    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    MachineResponse toResponse(Machine machine);
}

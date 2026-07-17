package com.weg.Maintenance_API.machine.mapper;

import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.machine.dto.request.MachineRequest;
import com.weg.Maintenance_API.machine.dto.response.MachineResponse;
import com.weg.Maintenance_API.machine.entity.Machine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MachineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "place", source = "placeId", qualifiedByName = "placeFromId")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "lastMaintenance", ignore = true)
    @Mapping(target = "machineLogs", ignore = true)
    Machine toEntity(MachineRequest machineRequest);

    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    MachineResponse toResponse(Machine machine);

    @Named("placeFromId")
    default Place placeFromId(Long id) {
        if (id == null) {
            return null;
        }

        Place place = new Place();
        place.setId(id);
        return place;
    }
}



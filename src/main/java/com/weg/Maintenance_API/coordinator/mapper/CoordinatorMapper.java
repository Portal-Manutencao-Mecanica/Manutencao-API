package com.weg.Maintenance_API.coordinator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;

@Mapper(componentModel = "spring")
public interface CoordinatorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Coordinator toEntity(CoordinatorRequestDto coordinatorRequestDto);

    CoordinatorResponseDto toResponse(Coordinator coordinator);
}


package com.weg.Maintenance_API.coordinator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;

@Mapper
public interface CoordinatorMapper {

    @Mapping(target = "id", ignore = true)
    Coordinator toEntity(CoordinatorRequestDto coordinatorRequestDto);

    CoordinatorResponseDto toResponse(Coordinator coordinator);
}



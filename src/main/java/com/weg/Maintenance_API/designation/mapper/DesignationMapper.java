package com.weg.Maintenance_API.designation.mapper;

import com.weg.Maintenance_API.designation.dto.request.DesignationDtoRequest;
import com.weg.Maintenance_API.designation.dto.response.DesignationDtoResponse;
import com.weg.Maintenance_API.designation.entity.Designation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DesignationMapper {

    @Mapping(target = "id", ignore = true)
    Designation toEntity(DesignationDtoRequest request);


    DesignationDtoResponse toResponse(Designation designation);
}


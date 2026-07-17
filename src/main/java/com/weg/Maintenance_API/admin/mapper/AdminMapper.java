package com.weg.Maintenance_API.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.admin.dto.request.AdminRequestDto;
import com.weg.Maintenance_API.admin.dto.response.AdminResponseDto;
import com.weg.Maintenance_API.admin.entity.Admin;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Admin toEntity(AdminRequestDto adminRequestDto);

    AdminResponseDto toResponse(Admin admin);
}

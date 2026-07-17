package com.weg.Manutencao_API.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Manutencao_API.admin.dto.request.AdminRequestDto;
import com.weg.Manutencao_API.admin.dto.response.AdminResponseDto;
import com.weg.Manutencao_API.admin.entity.Admin;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "id", ignore = true)
    Admin toEntity(AdminRequestDto adminRequestDto);

    AdminResponseDto toResponse(Admin admin);
}

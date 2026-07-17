package com.weg.Maintenance_API.classgroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    
    @Mapping(target = "id", ignore = true)
    ClassGroup toEntity(ClassRequestDto classRequestDto);

    ClassResponseDto toResponse(ClassGroup classGroup);
}



package com.weg.Maintenance_API.buy.mapper;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.media.mapper.MediaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BuyItemMapper.class, MediaMapper.class})
public interface BuyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "notifiedTeacher", ignore = true)
    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "items", source = "items")
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Buy toEntity(BuyDtoRequest request);

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.name")
    @Mapping(target = "notifiedTeacherId", source = "notifiedTeacher.id")
    @Mapping(target = "notifiedTeacherName", source = "notifiedTeacher.name")
    @Mapping(target = "classGroupId", source = "classGroup.id")
    @Mapping(target = "classGroupAcronym", source = "classGroup.acronym")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "media", source = "media")
    BuyDtoResponse toResponse(Buy buy);
}

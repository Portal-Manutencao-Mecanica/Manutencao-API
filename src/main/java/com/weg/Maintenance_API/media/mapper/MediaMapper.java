package com.weg.Maintenance_API.media.mapper;

import com.weg.Maintenance_API.media.dto.response.MediaResponseDto;
import com.weg.Maintenance_API.media.entity.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "numberCard", source = "numberCard")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "mediaType", source = "mediaType")
    @Mapping(target = "image", source = "image")
    @Mapping(target = "originalName", source = "originalName")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "fileSize", source = "fileSize")
    @Mapping(target = "createdAt", source = "createdAt")
    MediaResponseDto toResponse(Media media);
}

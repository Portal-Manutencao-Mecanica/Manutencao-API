package com.weg.Maintenance_API.event.mapper;

import com.weg.Maintenance_API.event.dto.response.CalendarResponseDto;
import com.weg.Maintenance_API.event.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    @Mapping(target = "equipmentId", source = "equipment.id")
    @Mapping(target = "equipmentName", source = "equipment.name")
    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "machineName", source = "machine.name")
    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    CalendarResponseDto toResponse(Event event);
}
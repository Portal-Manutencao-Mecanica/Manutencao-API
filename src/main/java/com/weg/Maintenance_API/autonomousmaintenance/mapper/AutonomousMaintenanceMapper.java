package com.weg.Maintenance_API.autonomousmaintenance.mapper;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutonomousMaintenanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inspectedEquipment", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "student", ignore = true)
    // Relationships must be resolved in the service layer.
    AutonomousMaintenance toEntity(AutonomousMaintenanceDtoRequest request);

    @Mapping(target = "equipmentId", source = "inspectedEquipment.id")
    @Mapping(target = "equipmentName", source = "inspectedEquipment.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    AutonomousMaintenanceDtoResponse toResponse(AutonomousMaintenance autonomousMaintenance);

}


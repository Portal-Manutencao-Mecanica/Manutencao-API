package com.weg.Maintenance_API.autonomousmaintenance.mapper;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutonomousMaintenanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inspectedMachine", ignore = true)
    @Mapping(target = "responsibleTeacher", ignore = true)
    @Mapping(target = "responsibleStudent", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "media", ignore = true)
    AutonomousMaintenance toEntity(AutonomousMaintenanceDtoRequest request);

    @Mapping(target = "inspectedMachineId", source = "inspectedMachine.id")
    @Mapping(target = "inspectedMachineName", source = "inspectedMachine.name")
    @Mapping(target = "responsibleTeacherId", source = "responsibleTeacher.id")
    @Mapping(target = "responsibleTeacherName", source = "responsibleTeacher.name")
    @Mapping(target = "responsibleStudentId", source = "responsibleStudent.id")
    @Mapping(target = "responsibleStudentName", source = "responsibleStudent.name")
    AutonomousMaintenanceDtoResponse toResponse(AutonomousMaintenance autonomousMaintenance);
}

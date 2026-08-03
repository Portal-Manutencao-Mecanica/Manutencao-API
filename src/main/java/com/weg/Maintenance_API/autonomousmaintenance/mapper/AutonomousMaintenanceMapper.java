package com.weg.Maintenance_API.autonomousmaintenance.mapper;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceStudentResponse;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.student.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutonomousMaintenanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inspectedMachine", ignore = true)
    @Mapping(target = "responsibleTeacher", ignore = true)
    @Mapping(target = "assignedStudents", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "coordinatorApprover", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "calendarEvent", ignore = true)
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AutonomousMaintenance toEntity(AutonomousMaintenanceDtoRequest request);

    @Mapping(target = "inspectedMachineId", source = "inspectedMachine.id")
    @Mapping(target = "inspectedMachineName", source = "inspectedMachine.name")
    @Mapping(target = "responsibleTeacherId", source = "responsibleTeacher.id")
    @Mapping(target = "responsibleTeacherName", source = "responsibleTeacher.name")
    @Mapping(target = "students", source = "assignedStudents")
    @Mapping(target = "coordinatorApproverId", source = "coordinatorApprover.id")
    @Mapping(target = "coordinatorApproverName", source = "coordinatorApprover.name")
    @Mapping(target = "calendarEventId", source = "calendarEvent.id")
    AutonomousMaintenanceDtoResponse toResponse(AutonomousMaintenance maintenance);

    AutonomousMaintenanceStudentResponse toStudentResponse(Student student);
}

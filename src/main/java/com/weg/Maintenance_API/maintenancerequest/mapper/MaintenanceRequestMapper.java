package com.weg.Maintenance_API.maintenancerequest.mapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.media.mapper.MediaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = MediaMapper.class)
public interface MaintenanceRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedStudents", ignore = true)
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "notifiedTeacher", ignore = true)
    @Mapping(target = "machine", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "workOrderNumber", ignore = true)
    @Mapping(target = "workOrderCreatedAt", ignore = true)
    @Mapping(target = "workOrderCreatedBy", ignore = true)
    @Mapping(target = "coordinatorApprovedBy", ignore = true)
    @Mapping(target = "coordinatorApprovedAt", ignore = true)
    @Mapping(target = "coordinatorRejectionReason", ignore = true)
    MaintenanceRequest toEntity(MaintenanceRequestRequest request);

    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "notifiedTeacherId", source = "notifiedTeacher.id")
    @Mapping(target = "notifiedTeacherName", source = "notifiedTeacher.name")
    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "machineName", source = "machine.name")
    @Mapping(target = "assignedStudentIds", source = "assignedStudents", qualifiedByName = "studentIdsFromStudents")
    @Mapping(target = "approvedById", source = "approvedBy.id")
    @Mapping(target = "approvedByName", source = "approvedBy.name")
    @Mapping(target = "workOrderCreatedById", source = "workOrderCreatedBy.id")
    @Mapping(target = "workOrderCreatedByName", source = "workOrderCreatedBy.name")
    @Mapping(target = "coordinatorApprovedById", source = "coordinatorApprovedBy.id")
    @Mapping(target = "coordinatorApprovedByName", source = "coordinatorApprovedBy.name")
    @Mapping(target = "media", source = "media")
    MaintenanceRequestResponse toResponse(MaintenanceRequest maintenanceRequest);

    @Named("studentIdsFromStudents")
    default List<UUID> studentIdsFromStudents(List<Student> students) {
        if (students == null) {
            return Collections.emptyList();
        }
        return students.stream().map(Student::getId).toList();
    }
}

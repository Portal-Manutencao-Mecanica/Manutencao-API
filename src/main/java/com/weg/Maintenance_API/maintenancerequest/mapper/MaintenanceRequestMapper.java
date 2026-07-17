package com.weg.Maintenance_API.maintenancerequest.mapper;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MaintenanceRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "designation", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    @Mapping(target = "dateTime", ignore = true)
    // Relationships must be resolved in the service layer.
    MaintenanceRequest toEntity(MaintenanceRequestRequest maintenanceRequestRequest);

    @Mapping(target = "designationId", source = "designation.id")
    @Mapping(target = "designationSector", source = "designation.sector")
    @Mapping(target = "studentIds", source = "students", qualifiedByName = "studentIdsFromStudents")
    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    @Mapping(target = "machineId", source = "equipment.id")
    @Mapping(target = "machineName", source = "equipment.name")
    MaintenanceRequestResponse toResponse(MaintenanceRequest maintenanceRequest);

    @Named("studentIdsFromStudents")
    default List<Long> studentIdsFromStudents(List<Student> students) {
        if (students == null) {
            return Collections.emptyList();
        }
        return students.stream().map(Student::getId).toList();
    }
}


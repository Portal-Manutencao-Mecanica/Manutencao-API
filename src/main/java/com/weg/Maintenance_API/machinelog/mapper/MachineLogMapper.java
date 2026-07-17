package com.weg.Maintenance_API.machinelog.mapper;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MachineLogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "machine", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "assignedStudents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "conclusion", ignore = true)
    // Relationships must be resolved in the service layer.
    MachineLog toEntity(MachineLogRequest machineLogRequest);

    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "machineName", source = "machine.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "classGroupId", source = "classGroup.id")
    @Mapping(target = "classGroupAcronym", source = "classGroup.acronym")
    @Mapping(target = "assignedStudentIds", source = "assignedStudents", qualifiedByName = "studentIdsFromStudents")
    MachineLogResponse toResponse(MachineLog machineLog);

    @Named("studentIdsFromStudents")
    default List<Long> studentIdsFromStudents(List<Student> students) {
        if (students == null) {
            return Collections.emptyList();
        }
        return students.stream().map(Student::getId).toList();
    }
}


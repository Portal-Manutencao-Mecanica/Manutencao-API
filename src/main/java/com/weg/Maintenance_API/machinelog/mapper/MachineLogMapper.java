package com.weg.Maintenance_API.machinelog.mapper;

import java.util.UUID;

import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.student.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MachineLogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "machine", ignore = true)
    @Mapping(target = "responsibleTeacher", ignore = true)
    @Mapping(target = "teacherConcludedAt", source = "teacherConcludedAt")
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "assignedStudents", ignore = true)
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    MachineLog toEntity(MachineLogRequest request);

    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "machineName", source = "machine.name")
    @Mapping(target = "responsibleTeacherId", source = "responsibleTeacher.id")
    @Mapping(target = "responsibleTeacherName", source = "responsibleTeacher.name")
    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "classGroupId", source = "classGroup.id")
    @Mapping(target = "classGroupAcronym", source = "classGroup.acronym")
    @Mapping(target = "assignedStudentIds", source = "assignedStudents", qualifiedByName = "studentIdsFromStudents")
    MachineLogResponse toResponse(MachineLog machineLog);

    @Named("studentIdsFromStudents")
    default List<UUID> studentIdsFromStudents(List<Student> students) {
        if (students == null) {
            return Collections.emptyList();
        }
        return students.stream().map(Student::getId).toList();
    }
}

package com.weg.Maintenance_API.maintenancerequest.mapper;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.designation.entity.Designation;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.teacher.entity.Teacher;
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
    @Mapping(target = "designation", source = "designationId", qualifiedByName = "designationFromId")
    @Mapping(target = "students", source = "studentIds", qualifiedByName = "studentsFromIds")
    @Mapping(target = "place", source = "placeId", qualifiedByName = "placeFromId")
    @Mapping(target = "teacher", source = "teacherId", qualifiedByName = "teacherFromId")
    @Mapping(target = "equipment", source = "machineId", qualifiedByName = "machineFromId")
    @Mapping(target = "dateTime", expression = "java(java.time.LocalDateTime.now())")
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

    @Named("designationFromId")
    default Designation designationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Designation designation = new Designation();
        designation.setId(id);
        return designation;
    }

    @Named("placeFromId")
    default Place placeFromId(Long id) {
        if (id == null) {
            return null;
        }
        Place place = new Place();
        place.setId(id);
        return place;
    }

    @Named("teacherFromId")
    default Teacher teacherFromId(Long id) {
        if (id == null) {
            return null;
        }
        Teacher teacher = new Teacher();
        teacher.setId(id);
        return teacher;
    }

    @Named("machineFromId")
    default Machine machineFromId(Integer id) {
        if (id == null) {
            return null;
        }
        Machine machine = new Machine();
        machine.setId(id);
        return machine;
    }

    @Named("studentsFromIds")
    default List<Student> studentsFromIds(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream().map(this::studentFromId).toList();
    }

    default Student studentFromId(Long id) {
        if (id == null) {
            return null;
        }
        Student student = new Student();
        student.setId(id);
        return student;
    }

    @Named("studentIdsFromStudents")
    default List<Long> studentIdsFromStudents(List<Student> students) {
        if (students == null) {
            return Collections.emptyList();
        }
        return students.stream().map(Student::getId).toList();
    }
}



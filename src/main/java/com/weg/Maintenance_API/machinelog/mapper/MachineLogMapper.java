package com.weg.Maintenance_API.machinelog.mapper;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.machinelog.entity.MachineLog;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MachineLogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "machine", source = "machineId", qualifiedByName = "machineFromId")
    @Mapping(target = "teacher", source = "teacherId", qualifiedByName = "teacherFromId")
    @Mapping(target = "place", source = "placeId", qualifiedByName = "placeFromId")
    @Mapping(target = "classGroup", source = "classGroupId", qualifiedByName = "classGroupFromId")
    @Mapping(target = "assignedStudents", source = "assignedStudentIds", qualifiedByName = "studentsFromIds")
    @Mapping(target = "registrationDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "conclusion", ignore = true)
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

    @Named("machineFromId")
    default Machine machineFromId(Integer id) {
        if (id == null) {
            return null;
        }
        Machine machine = new Machine();
        machine.setId(id);
        return machine;
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

    @Named("placeFromId")
    default Place placeFromId(Long id) {
        if (id == null) {
            return null;
        }
        Place place = new Place();
        place.setId(id);
        return place;
    }

    @Named("classGroupFromId")
    default ClassGroup classGroupFromId(Long id) {
        if (id == null) {
            return null;
        }
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(id);
        return classGroup;
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



package com.weg.Maintenance_API.inconvenience5s.mapper;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.response.Inconvenience5SDtoResponse;
import com.weg.Maintenance_API.inconvenience5s.entity.Inconvenience5S;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface Inconvenience5SMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", source = "placeId", qualifiedByName = "placeFromId")
    @Mapping(target = "teacher", source = "teacherId", qualifiedByName = "teacherFromId")
    @Mapping(target = "classGroup", source = "classGroupId", qualifiedByName = "classGroupFromId")
    @Mapping(target = "students", source = "studentIds", qualifiedByName = "studentsFromIds")
    Inconvenience5S toEntity(Inconvenience5SDtoRequest request);

    @Mapping(target = "placeId", source = "room.id")
    @Mapping(target = "placeName", source = "room.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    @Mapping(target = "classGroupId", source = "classGroup.id")
    @Mapping(target = "classGroupAcronym", source = "classGroup.acronym")
    @Mapping(target = "studentIds", source = "students", qualifiedByName = "studentIdsFromStudents")
    Inconvenience5SDtoResponse toResponse(Inconvenience5S inconvenience5S);

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

        return ids.stream()
                .map(this::studentFromId)
                .toList();
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

        return students.stream()
                .map(Student::getId)
                .toList();
    }
}



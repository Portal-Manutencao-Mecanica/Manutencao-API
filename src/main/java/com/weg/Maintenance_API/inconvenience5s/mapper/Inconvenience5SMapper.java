package com.weg.Maintenance_API.inconvenience5s.mapper;

import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.response.Inconvenience5SDtoResponse;
import com.weg.Maintenance_API.inconvenience5s.entity.Inconvenience5S;
import com.weg.Maintenance_API.student.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface Inconvenience5SMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "place", ignore = true)
    @Mapping(target = "notifiedTeacher", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "involvedStudents", ignore = true)
    @Mapping(target = "media", ignore = true)
    Inconvenience5S toEntity(Inconvenience5SDtoRequest request);

    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "notifiedTeacherId", source = "notifiedTeacher.id")
    @Mapping(target = "notifiedTeacherName", source = "notifiedTeacher.name")
    @Mapping(target = "classGroupId", source = "classGroup.id")
    @Mapping(target = "classGroupAcronym", source = "classGroup.acronym")
    @Mapping(target = "involvedStudentIds", source = "involvedStudents", qualifiedByName = "studentIdsFromStudents")
    Inconvenience5SDtoResponse toResponse(Inconvenience5S inconvenience5S);

    @Named("studentIdsFromStudents")
    default List<Long> studentIdsFromStudents(List<Student> students) {
        if (students == null) {
            return Collections.emptyList();
        }
        return students.stream().map(Student::getId).toList();
    }
}

package com.weg.Maintenance_API.classgroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.student.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;
import com.weg.Maintenance_API.teacher.entity.Teacher;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassGroupMapper {

    TeacherResponseDto toTeacherResponse(Teacher teacher);

    StudentDtoResponse toStudentResponse(Student student);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "students", ignore = true)
    // Relationships must be resolved in the service layer.
    ClassGroup toEntity(ClassRequestDto classRequestDto);

    default ClassResponseDto toResponse(ClassGroup classGroup) {
        if (classGroup == null) {
            return null;
        }

        List<TeacherResponseDto> teachers = classGroup.getTeachers().stream()
                .map(this::toTeacherResponse)
                .toList();
        List<StudentDtoResponse> students = classGroup.getStudents().stream()
                .map(this::toStudentResponse)
                .toList();

        return new ClassResponseDto(classGroup.getId(), classGroup.getNumberCard(), classGroup.getAcronym(), classGroup.isEnabled(), teachers, students);
    }
}

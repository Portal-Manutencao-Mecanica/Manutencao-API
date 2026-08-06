package com.weg.Maintenance_API.classgroup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.classgroup.mapper.ClassGroupMapper;
import com.weg.Maintenance_API.classgroup.repository.ClassGroupRepository;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.teacher.repository.TeacherRepository;

@ExtendWith(MockitoExtension.class)
class ClassGroupServiceTest {

    @Mock
    private ClassGroupRepository classGroupRepository;

    @Mock
    private ClassGroupMapper classGroupMapper;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ClassGroupService classGroupService;

    @Test
    void createAssociatesSelectedTeachersAndStudents() {
        UUID teacherId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        ClassRequestDto request = new ClassRequestDto("MEC-2026", List.of(teacherId), List.of(studentId));
        ClassGroup classGroup = new ClassGroup();
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        Student student = new Student();
        student.setId(studentId);

        when(classGroupMapper.toEntity(request)).thenReturn(classGroup);
        when(teacherRepository.findAllById(List.of(teacherId))).thenReturn(List.of(teacher));
        when(studentRepository.findAllById(List.of(studentId))).thenReturn(List.of(student));

        classGroupService.create(request);

        assertThat(classGroup.getTeachers()).containsExactly(teacher);
        assertThat(classGroup.getStudents()).containsExactly(student);
    }

    @Test
    void reactivateMarksTheClassGroupAsEnabled() {
        UUID classGroupId = UUID.randomUUID();
        ClassGroup classGroup = new ClassGroup();
        classGroup.setEnabled(false);

        when(classGroupRepository.findById(classGroupId)).thenReturn(Optional.of(classGroup));

        classGroupService.reativar(classGroupId);

        assertThat(classGroup.isEnabled()).isTrue();
    }

    @Test
    void createRejectsStudentAlreadyAssignedToAnotherClassGroup() {
        UUID studentId = UUID.randomUUID();
        ClassRequestDto request = new ClassRequestDto("MEC-2026", List.of(), List.of(studentId));
        ClassGroup classGroup = new ClassGroup();
        ClassGroup existingClassGroup = new ClassGroup();
        existingClassGroup.setId(UUID.randomUUID());
        Student student = new Student();
        student.setId(studentId);
        student.setName("Aluno já alocado");
        student.setClassGroups(List.of(existingClassGroup));

        when(classGroupMapper.toEntity(request)).thenReturn(classGroup);
        when(teacherRepository.findAllById(List.of())).thenReturn(List.of());
        when(studentRepository.findAllById(List.of(studentId))).thenReturn(List.of(student));

        assertThatThrownBy(() -> classGroupService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("já pertence a outra turma");
    }
}

package com.weg.Maintenance_API.classgroup.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import com.weg.Maintenance_API.classgroup.dto.request.ClassPatchRequest;
import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.classgroup.mapper.ClassGroupMapper;
import com.weg.Maintenance_API.classgroup.repository.ClassGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.teacher.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class ClassGroupService {
    private final ClassGroupRepository repository;
    private final ClassGroupMapper mapper;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    // Cria e persiste os dados da operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto create(ClassRequestDto request){
        ClassGroup entity = mapper.toEntity(request);
        entity.setTeachers(resolveTeachers(request.teacherIds()));
        entity.setStudents(resolveStudents(request.studentIds()));
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public org.springframework.data.domain.Page<ClassResponseDto> getAll(
            String search,
            Boolean enabled,
            org.springframework.data.domain.Pageable pageable
    ) {
        String normalizedSearch = search == null || search.isBlank()
                ? null
                : search.trim();
        return repository.findAllFiltered(
                normalizedSearch,
                enabled,
                pageable
        ).map(mapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public org.springframework.data.domain.Page<ClassResponseDto> getAllAtivos(
            org.springframework.data.domain.Pageable pageable
    ) {
        return repository.findAllByEnabledTrue(pageable).map(mapper::toResponse);
    }

    // Executa a operacao deste metodo.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto inativar(UUID id){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        entity.setEnabled(false);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto reativar(UUID id){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        entity.setEnabled(true);
        return mapper.toResponse(entity);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ClassResponseDto getById(UUID id){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto update(UUID id, ClassRequestDto request){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        entity.setAcronym(request.acronym());
        entity.setTeachers(resolveTeachers(request.teacherIds()));
        entity.setStudents(resolveStudents(request.studentIds()));
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto patch(UUID id, ClassPatchRequest request){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));

        if (request.acronym() != null) {
            entity.setAcronym(request.acronym());
        }

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Remove ou invalida os dados solicitados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteById(UUID id){
        repository.delete(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id)));
    }

    private List<Teacher> resolveTeachers(List<UUID> teacherIds) {
        List<UUID> ids = distinctIds(teacherIds);
        List<Teacher> teachers = teacherRepository.findAllById(ids);
        validateResolvedIds(ids, teachers.stream().map(Teacher::getId).toList(), "Professor");
        return new ArrayList<>(teachers);
    }

    private List<Student> resolveStudents(List<UUID> studentIds) {
        List<UUID> ids = distinctIds(studentIds);
        List<Student> students = studentRepository.findAllById(ids);
        validateResolvedIds(ids, students.stream().map(Student::getId).toList(), "Aluno");
        return new ArrayList<>(students);
    }

    private List<UUID> distinctIds(List<UUID> ids) {
        return ids == null ? List.of() : ids.stream().distinct().toList();
    }

    private void validateResolvedIds(List<UUID> requestedIds, List<UUID> resolvedIds, String resourceName) {
        Set<UUID> foundIds = new HashSet<>(resolvedIds);
        requestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new ResourceNotFoundException(resourceName, id);
                });
    }
}

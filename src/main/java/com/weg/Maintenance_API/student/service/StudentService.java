package com.weg.Maintenance_API.student.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.weg.Maintenance_API.student.dto.request.StudentDtoRequest;
import com.weg.Maintenance_API.student.dto.request.StudentPatchRequest;
import com.weg.Maintenance_API.student.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.student.mapper.StudentMapper;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.user.service.UserIdentityPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserIdentityPolicy identityPolicy;

    // Cria e persiste os dados da operacao.
    @Transactional
    public StudentDtoResponse save(StudentDtoRequest studentDtoRequest) {
        identityPolicy.validateEmailAvailable(studentDtoRequest.email());
        Student student = studentMapper.toEntity(studentDtoRequest);
        student.setPassword(passwordEncoder.encode(studentDtoRequest.password()));
        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<StudentDtoResponse> getAll(
            String search,
            Boolean enabled,
            org.springframework.data.domain.Pageable pageable
    ) {
        String normalizedSearch = search == null || search.isBlank()
                ? null
                : search.trim();
        return studentRepository.findAllFiltered(
                normalizedSearch,
                enabled,
                pageable
        ).map(studentMapper::toResponse);
    }
    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<StudentDtoResponse> getAllAtivos(
            org.springframework.data.domain.Pageable pageable
    ) {
        return studentRepository.findAllByEnabledTrue(pageable).map(studentMapper::toResponse);
    }

    // Executa a operacao deste metodo.
    @Transactional
    public StudentDtoResponse inativar(UUID id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        student.setEnabled(false);
        return studentMapper.toResponse(studentRepository.save(student));
    }
    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public StudentDtoResponse getById(UUID id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        return studentMapper.toResponse(student);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public StudentDtoResponse update(UUID id, StudentDtoRequest studentDtoRequest) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        student.setName(studentDtoRequest.name());
        student.setEmail(studentDtoRequest.email());
        student.setPassword(passwordEncoder.encode(studentDtoRequest.password()));
        return studentMapper.toResponse(studentRepository.save(student));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public StudentDtoResponse patch(UUID id, StudentPatchRequest request) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));

        if (request.name() != null) {
            student.setName(request.name());
        }
        if (request.email() != null) {
            student.setEmail(request.email());
        }
        if (request.password() != null) {
            student.setPassword(passwordEncoder.encode(request.password()));
        }

        return studentMapper.toResponse(studentRepository.save(student));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        studentRepository.deleteById(id);
    }
}

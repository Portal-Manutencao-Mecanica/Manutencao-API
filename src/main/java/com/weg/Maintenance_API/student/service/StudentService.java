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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StudentDtoResponse save(StudentDtoRequest studentDtoRequest) {
        Student student = studentMapper.toEntity(studentDtoRequest);
        student.setPassword(passwordEncoder.encode(studentDtoRequest.password()));
        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDtoResponse> getAll() {
        return studentRepository.findAll().stream().map(studentMapper::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<StudentDtoResponse> getAllAtivos() {
        return studentRepository.findAllByEnabledTrue().stream().map(studentMapper::toResponse).toList();
    }

    @Transactional
    public StudentDtoResponse inativar(UUID id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        student.setEnabled(false);
        return studentMapper.toResponse(studentRepository.save(student));
    }
    @Transactional(readOnly = true)
    public StudentDtoResponse getById(UUID id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        return studentMapper.toResponse(student);
    }

    @Transactional
    public StudentDtoResponse update(UUID id, StudentDtoRequest studentDtoRequest) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        student.setName(studentDtoRequest.name());
        student.setEmail(studentDtoRequest.email());
        student.setPassword(passwordEncoder.encode(studentDtoRequest.password()));
        return studentMapper.toResponse(studentRepository.save(student));
    }

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

    @Transactional
    public void delete(UUID id) {
        studentRepository.deleteById(id);
    }
}

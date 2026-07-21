package com.weg.Maintenance_API.student.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public StudentDtoResponse save(StudentDtoRequest studentDtoRequest) {
        Student student = studentMapper.toEntity(studentDtoRequest);
        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDtoResponse> getAll() {
        return studentRepository.findAll().stream().map(studentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentDtoResponse getById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return studentMapper.toResponse(student);
    }

    @Transactional
    public StudentDtoResponse update(Long id, StudentDtoRequest studentDtoRequest) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        student.setName(studentDtoRequest.name());
        student.setEmail(studentDtoRequest.email());
        student.setPassword(studentDtoRequest.password());
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentDtoResponse patch(Long id, StudentPatchRequest request) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        if (request.name() != null) {
            student.setName(request.name());
        }
        if (request.email() != null) {
            student.setEmail(request.email());
        }
        if (request.password() != null) {
            student.setPassword(request.password());
        }

        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}
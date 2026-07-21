package com.weg.Maintenance_API.student.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.Maintenance_API.student.dto.request.StudentDtoRequest;
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

    public StudentDtoResponse save(StudentDtoRequest studentDtoRequest) {
        Student student = studentMapper.toEntity(studentDtoRequest);

        student = studentRepository.save(student);

        return studentMapper.toResponse(student);
    }

    public List<StudentDtoResponse> getAll() {
        List<Student> students = studentRepository.findAll();

        return students.stream().map(studentMapper::toResponse).toList();
    }
}

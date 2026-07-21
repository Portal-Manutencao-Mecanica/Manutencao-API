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

    public StudentDtoResponse getById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("")); // Falta a
                                                                                                      // mensagem

        return studentMapper.toResponse(student);
    }

    public StudentDtoResponse update(Long id, StudentDtoRequest studentDtoRequest) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("")); // Falta a
                                                                                                      // mensagem

        student.setName(studentDtoRequest.name());
        student.setEmail(studentDtoRequest.email());
        student.setPassword(studentDtoRequest.password());

        student = studentRepository.save(student);

        return studentMapper.toResponse(student);
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}

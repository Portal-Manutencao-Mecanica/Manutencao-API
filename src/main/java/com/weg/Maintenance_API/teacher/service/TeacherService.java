package com.weg.Maintenance_API.teacher.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.Maintenance_API.teacher.dto.request.TeacherRequestDto;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.teacher.mapper.TeacherMapper;
import com.weg.Maintenance_API.teacher.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherMapper teacherMapper;
    private final TeacherRepository teacherRepository;

    public TeacherResponseDto save(TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherMapper.toEntity(teacherRequestDto);

        teacher = teacherRepository.save(teacher);

        return teacherMapper.toResponse(teacher);
    }

    public List<TeacherResponseDto> getAll() {
        List<Teacher> teachers = teacherRepository.findAll();

        return teachers.stream().map(teacherMapper::toResponse).toList();
    }

    public TeacherResponseDto getById(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("")); // Falta a
                                                                                                      // mensagem

        return teacherMapper.toResponse(teacher);
    }

    public TeacherResponseDto update(TeacherRequestDto teacherRequestDto, Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("")); // Falta a
                                                                                                      // mensagem

        teacher.setName(teacherRequestDto.name());
        teacher.setEmail(teacherRequestDto.email());
        teacher.setPassword(teacherRequestDto.password());

        teacher = teacherRepository.save(teacher);

        return teacherMapper.toResponse(teacher);
    }

    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }
}
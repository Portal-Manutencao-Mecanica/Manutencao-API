package com.weg.Maintenance_API.teacher.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.teacher.dto.request.TeacherPatchRequest;
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

    @Transactional
    public TeacherResponseDto save(TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherMapper.toEntity(teacherRequestDto);
        teacher = teacherRepository.save(teacher);
        return teacherMapper.toResponse(teacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getAll() {
        return teacherRepository.findAll().stream().map(teacherMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TeacherResponseDto getById(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return teacherMapper.toResponse(teacher);
    }

    @Transactional
    public TeacherResponseDto update(TeacherRequestDto teacherRequestDto, Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        teacher.setName(teacherRequestDto.name());
        teacher.setEmail(teacherRequestDto.email());
        teacher.setPassword(teacherRequestDto.password());
        return teacherMapper.toResponse(teacherRepository.save(teacher));
    }

    @Transactional
    public TeacherResponseDto patch(Long id, TeacherPatchRequest request) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        if (request.name() != null) {
            teacher.setName(request.name());
        }
        if (request.email() != null) {
            teacher.setEmail(request.email());
        }
        if (request.password() != null) {
            teacher.setPassword(request.password());
        }

        return teacherMapper.toResponse(teacherRepository.save(teacher));
    }

    @Transactional
    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }
}
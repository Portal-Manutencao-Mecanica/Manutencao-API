package com.weg.Maintenance_API.teacher.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TeacherResponseDto save(TeacherRequestDto teacherRequestDto) {
        Teacher teacher = teacherMapper.toEntity(teacherRequestDto);
        teacher.setPassword(passwordEncoder.encode(teacherRequestDto.password()));
        teacher = teacherRepository.save(teacher);
        return teacherMapper.toResponse(teacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getAll() {
        return teacherRepository.findAll().stream().map(teacherMapper::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getAllAtivos() {
        return teacherRepository.findAllByEnabledTrue().stream().map(teacherMapper::toResponse).toList();
    }

    @Transactional
    public TeacherResponseDto inativar(UUID id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id));
        teacher.setEnabled(false);
        return teacherMapper.toResponse(teacherRepository.save(teacher));
    }
    @Transactional(readOnly = true)
    public TeacherResponseDto getById(UUID id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id));
        return teacherMapper.toResponse(teacher);
    }

    @Transactional
    public TeacherResponseDto update(TeacherRequestDto teacherRequestDto, UUID id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id));
        teacher.setName(teacherRequestDto.name());
        teacher.setEmail(teacherRequestDto.email());
        teacher.setPassword(passwordEncoder.encode(teacherRequestDto.password()));
        return teacherMapper.toResponse(teacherRepository.save(teacher));
    }

    @Transactional
    public TeacherResponseDto patch(UUID id, TeacherPatchRequest request) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor", id));

        if (request.name() != null) {
            teacher.setName(request.name());
        }
        if (request.email() != null) {
            teacher.setEmail(request.email());
        }
        if (request.password() != null) {
            teacher.setPassword(passwordEncoder.encode(request.password()));
        }

        return teacherMapper.toResponse(teacherRepository.save(teacher));
    }

    @Transactional
    public void delete(UUID id) {
        teacherRepository.deleteById(id);
    }
}

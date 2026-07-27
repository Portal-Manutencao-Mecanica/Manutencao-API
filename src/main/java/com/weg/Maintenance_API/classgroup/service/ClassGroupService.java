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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassGroupService {
    private final ClassGroupRepository repository;
    private final ClassGroupMapper mapper;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public ClassResponseDto create(ClassRequestDto request){
        ClassGroup entity = mapper.toEntity(request);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public List<ClassResponseDto> getAll(){
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public List<ClassResponseDto> getAllAtivos(){
        return repository.findAllByEnabledTrue().stream().map(mapper::toResponse).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public ClassResponseDto inativar(UUID id){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        entity.setEnabled(false);
        repository.save(entity);
        return mapper.toResponse(entity);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public ClassResponseDto getById(UUID id){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public ClassResponseDto update(UUID id, ClassRequestDto request){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        entity.setAcronym(request.acronym());
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public ClassResponseDto patch(UUID id, ClassPatchRequest request){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));

        if (request.acronym() != null) {
            entity.setAcronym(request.acronym());
        }

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public void deleteById(UUID id){
        repository.deleteById(id);
    }
}
package com.weg.Maintenance_API.coordinator.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorPatchRequest;
import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.coordinator.mapper.CoordinatorMapper;
import com.weg.Maintenance_API.coordinator.repository.CoordinatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordinatorService {
    private final CoordinatorRepository repository;
    private final CoordinatorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto create(CoordinatorRequestDto request){
        Coordinator entity = mapper.toEntity(request);
        entity.setPassword(passwordEncoder.encode(request.password()));
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public List<CoordinatorResponseDto> getAll(){
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public List<CoordinatorResponseDto> getAllAtivos(){
        return repository.findAllByEnabledTrue().stream().map(mapper::toResponse).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto inativar(UUID id){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));
        entity.setEnabled(false);
        repository.save(entity);
        return mapper.toResponse(entity);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public CoordinatorResponseDto getById(UUID id){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto update(UUID id, CoordinatorRequestDto request){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));
        entity.setName(request.name());
        entity.setEmail(request.email());
        entity.setPassword(passwordEncoder.encode(request.password()));
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto patch(UUID id, CoordinatorPatchRequest request){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.email() != null) {
            entity.setEmail(request.email());
        }
        if (request.password() != null) {
            entity.setPassword(passwordEncoder.encode(request.password()));
        }

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public void deleteById(UUID id){
        repository.deleteById(id);
    }
}

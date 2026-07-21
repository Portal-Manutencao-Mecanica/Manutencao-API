package com.weg.Maintenance_API.coordinator.service;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.coordinator.mapper.CoordinatorMapper;
import com.weg.Maintenance_API.coordinator.repository.CoordinatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordinatorService {
    private final CoordinatorRepository repository;
    private final CoordinatorMapper mapper;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto create(CoordinatorRequestDto request){
        Coordinator entity = mapper.toEntity(request);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class,readOnly = true)
    public List<CoordinatorResponseDto> getAll(){
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class,readOnly = true)
    public CoordinatorResponseDto getById(Long id){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return mapper.toResponse(entity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto update(Long id, CoordinatorRequestDto request){
        if(!repository.existsById(id)) throw new RuntimeException("");
        Coordinator entity = mapper.toEntity(request);
        entity.setId(id);
        repository.save(entity);
        return mapper.toResponse(entity);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public void deleteById(Long id){
        repository.deleteById(id);
    }
}


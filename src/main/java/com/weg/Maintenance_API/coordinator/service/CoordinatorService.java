package com.weg.Maintenance_API.coordinator.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorPatchRequest;
import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.coordinator.mapper.CoordinatorMapper;
import com.weg.Maintenance_API.coordinator.repository.CoordinatorRepository;
import com.weg.Maintenance_API.user.service.UserIdentityPolicy;
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
    private final UserIdentityPolicy identityPolicy;

    // Cria e persiste os dados da operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto create(CoordinatorRequestDto request){
        identityPolicy.validateEmailAvailable(request.email());
        Coordinator entity = mapper.toEntity(request);
        entity.setPassword(passwordEncoder.encode(request.password()));
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public org.springframework.data.domain.Page<CoordinatorResponseDto> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public org.springframework.data.domain.Page<CoordinatorResponseDto> getAllAtivos(
            org.springframework.data.domain.Pageable pageable
    ) {
        return repository.findAllByEnabledTrue(pageable).map(mapper::toResponse);
    }

    // Executa a operacao deste metodo.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto inativar(UUID id){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));
        entity.setEnabled(false);
        repository.save(entity);
        return mapper.toResponse(entity);
    }
    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, readOnly = true)
    public CoordinatorResponseDto getById(UUID id){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public CoordinatorResponseDto update(UUID id, CoordinatorRequestDto request){
        Coordinator entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));
        entity.setName(request.name());
        entity.setEmail(request.email());
        entity.setPassword(passwordEncoder.encode(request.password()));
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
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

    // Remove ou invalida os dados solicitados.
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class)
    public void deleteById(UUID id){
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coordenador", id));
        repository.deleteById(id);
    }
}

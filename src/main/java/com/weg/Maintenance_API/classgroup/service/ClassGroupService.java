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

    // Cria e persiste os dados da operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto create(ClassRequestDto request){
        ClassGroup entity = mapper.toEntity(request);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public org.springframework.data.domain.Page<ClassResponseDto> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public org.springframework.data.domain.Page<ClassResponseDto> getAllAtivos(
            org.springframework.data.domain.Pageable pageable
    ) {
        return repository.findAllByEnabledTrue(pageable).map(mapper::toResponse);
    }

    // Executa a operacao deste metodo.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto inativar(UUID id){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        entity.setEnabled(false);
        return mapper.toResponse(entity);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ClassResponseDto getById(UUID id){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto update(UUID id, ClassRequestDto request){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));
        entity.setAcronym(request.acronym());
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ClassResponseDto patch(UUID id, ClassPatchRequest request){
        ClassGroup entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id));

        if (request.acronym() != null) {
            entity.setAcronym(request.acronym());
        }

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // Remove ou invalida os dados solicitados.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteById(UUID id){
        repository.delete(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Turma", id)));
    }
}

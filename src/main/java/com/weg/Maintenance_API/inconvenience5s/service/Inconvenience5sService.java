package com.weg.Maintenance_API.inconvenience5s.service;


import java.util.UUID;

import com.weg.Maintenance_API.enums.RegistrationPeriod;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SPatchRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.response.Inconvenience5SDtoResponse;
import com.weg.Maintenance_API.inconvenience5s.entity.Inconvenience5S;
import com.weg.Maintenance_API.inconvenience5s.mapper.Inconvenience5SMapper;
import com.weg.Maintenance_API.inconvenience5s.repository.Inconvenience5sRepository;
import com.weg.Maintenance_API.service.EntityReferenceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Inconvenience5sService {

    private final Inconvenience5SMapper inconvenience5sMapper;
    private final Inconvenience5sRepository inconvenience5sRepository;
    private final EntityReferenceService references;

    // Cria e persiste os dados da operacao.
    @Transactional
    public Inconvenience5SDtoResponse save(Inconvenience5SDtoRequest request) {
        Inconvenience5S inconvenience5s = inconvenience5sMapper.toEntity(request);
        applyReferences(inconvenience5s, request);
        inconvenience5s = inconvenience5sRepository.save(inconvenience5s);
        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Inconvenience5SDtoResponse> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return inconvenience5sRepository.findAll(pageable).map(inconvenience5sMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public Inconvenience5SDtoResponse getById(UUID id) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id));
        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public Inconvenience5SDtoResponse update(UUID id, Inconvenience5SDtoRequest request) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id));
        applyReferences(inconvenience5s, request);
        inconvenience5s.setInconvenience(request.inconvenience());
        inconvenience5s.setDescription(request.description());
        inconvenience5s.setRegistrationPeriod(request.registrationPeriod());
        return inconvenience5sMapper.toResponse(inconvenience5sRepository.save(inconvenience5s));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public Inconvenience5SDtoResponse patch(UUID id, Inconvenience5SPatchRequest request) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id));

        if (request.inconvenience() != null) {
        inconvenience5s.setInconvenience(request.inconvenience());
        }
        if (request.description() != null) {
            inconvenience5s.setDescription(request.description());
        }
        if (request.registrationPeriod() != null) {
            inconvenience5s.setRegistrationPeriod(RegistrationPeriod.valueOf(
                    request.registrationPeriod().trim().toUpperCase(java.util.Locale.ROOT)
            ));
        }

        return inconvenience5sMapper.toResponse(inconvenience5sRepository.save(inconvenience5s));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        inconvenience5sRepository.delete(inconvenience5sRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inconvenience5S", id)));
    }
    // Aplica os dados recebidos na entidade.
    private void applyReferences(Inconvenience5S entity, Inconvenience5SDtoRequest request) {
        entity.setPlace(references.place(request.placeId()));
        entity.setNotifiedTeacher(references.teacher(request.notifiedTeacherId()));
        entity.setClassGroup(references.classGroup(request.classGroupId()));
        entity.setInvolvedStudents(references.students(request.involvedStudentIds()));
    }}

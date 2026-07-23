package com.weg.Maintenance_API.inconvenience5s.service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Inconvenience5sService {

    private final Inconvenience5SMapper inconvenience5sMapper;
    private final Inconvenience5sRepository inconvenience5sRepository;

    @Transactional
    public Inconvenience5SDtoResponse save(Inconvenience5SDtoRequest request) {
        Inconvenience5S inconvenience5s = inconvenience5sMapper.toEntity(request);
        inconvenience5s = inconvenience5sRepository.save(inconvenience5s);
        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    @Transactional(readOnly = true)
    public List<Inconvenience5SDtoResponse> getAll() {
        return inconvenience5sRepository.findAll().stream().map(inconvenience5sMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Inconvenience5SDtoResponse getById(Long id) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência 5S", id));
        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    @Transactional
    public Inconvenience5SDtoResponse update(Long id, Inconvenience5SDtoRequest request) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência 5S", id));
        inconvenience5s.setInconvenience(request.inconvenience());
        inconvenience5s.setDescription(request.description());
        inconvenience5s.setRegistrationPeriod(request.registrationPeriod());
        return inconvenience5sMapper.toResponse(inconvenience5sRepository.save(inconvenience5s));
    }

    @Transactional
    public Inconvenience5SDtoResponse patch(Long id, Inconvenience5SPatchRequest request) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência 5S", id));

        if (request.inconvenience() != null) {
            inconvenience5s.setInconvenience(request.inconvenience());
        }
        if (request.description() != null) {
            inconvenience5s.setDescription(request.description());
        }
        if (request.registrationPeriod() != null) {
            inconvenience5s.setRegistrationPeriod(request.registrationPeriod());
        }

        return inconvenience5sMapper.toResponse(inconvenience5sRepository.save(inconvenience5s));
    }

    @Transactional
    public void delete(Long id) {
        inconvenience5sRepository.deleteById(id);
    }
}
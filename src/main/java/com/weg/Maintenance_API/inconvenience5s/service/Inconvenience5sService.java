package com.weg.Maintenance_API.inconvenience5s.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
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
    public Inconvenience5SDtoResponse save(Inconvenience5SDtoRequest inconvenience5sDtoRequest) {
        Inconvenience5S inconvenience5s = inconvenience5sMapper.toEntity(inconvenience5sDtoRequest);

        inconvenience5s = inconvenience5sRepository.save(inconvenience5s);

        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    @Transactional(readOnly = true)
    public List<Inconvenience5SDtoResponse> getAll() {
        List<Inconvenience5S> inconvenience5s = inconvenience5sRepository.findAll();

        return inconvenience5s.stream().map(inconvenience5sMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Inconvenience5SDtoResponse getById(Long id) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(""));

        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    @Transactional
    public Inconvenience5SDtoResponse update(Long id, Inconvenience5SDtoRequest inconvenience5sDtoRequest) {
        Inconvenience5S inconvenience5s = inconvenience5sRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(""));

        inconvenience5s.setInconvenience(inconvenience5sDtoRequest.inconvenience());
        inconvenience5s.setDescription(inconvenience5sDtoRequest.description());
        inconvenience5s.setRegistrationPeriod(inconvenience5sDtoRequest.registrationPeriod());

        inconvenience5s = inconvenience5sRepository.save(inconvenience5s);

        return inconvenience5sMapper.toResponse(inconvenience5s);
    }

    @Transactional
    public void delete(Long id) {
        inconvenience5sRepository.deleteById(id);
    }
}

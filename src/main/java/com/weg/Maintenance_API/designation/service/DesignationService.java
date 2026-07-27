package com.weg.Maintenance_API.designation.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.designation.dto.request.DesignationPatchRequest;
import com.weg.Maintenance_API.designation.dto.request.DesignationDtoRequest;
import com.weg.Maintenance_API.designation.dto.response.DesignationDtoResponse;
import com.weg.Maintenance_API.designation.entity.Designation;
import com.weg.Maintenance_API.designation.mapper.DesignationMapper;
import com.weg.Maintenance_API.designation.repository.DesignationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DesignationService {

    private final DesignationMapper designationMapper;
    private final DesignationRepository designationRepository;

    @Transactional
    public DesignationDtoResponse save(DesignationDtoRequest designationDtoRequest) {
        Designation designation = designationMapper.toEntity(designationDtoRequest);
        designation = designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    @Transactional(readOnly = true)
    public List<DesignationDtoResponse> getAll() {
        return designationRepository.findAll().stream().map(designationMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DesignationDtoResponse getById(UUID id) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Designação", id));
        return designationMapper.toResponse(designation);
    }

    @Transactional
    public DesignationDtoResponse update(UUID id, DesignationDtoRequest designationDtoRequest) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Designação", id));
        designation.setSector(designationDtoRequest.sector());
        designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    @Transactional
    public DesignationDtoResponse patch(UUID id, DesignationPatchRequest request) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Designação", id));

        if (request.sector() != null) {
            designation.setSector(request.sector());
        }

        designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    @Transactional
    public void delete(UUID id) {
        designationRepository.deleteById(id);
    }
}
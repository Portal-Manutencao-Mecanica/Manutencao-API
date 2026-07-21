package com.weg.Maintenance_API.designation.service;

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
    public DesignationDtoResponse getById(Long id) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        return designationMapper.toResponse(designation);
    }

    @Transactional
    public DesignationDtoResponse update(Long id, DesignationDtoRequest designationDtoRequest) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        designation.setSector(designationDtoRequest.sector());
        designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    @Transactional
    public DesignationDtoResponse patch(Long id, DesignationPatchRequest request) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        if (request.sector() != null) {
            designation.setSector(request.sector());
        }

        designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    @Transactional
    public void delete(Long id) {
        designationRepository.deleteById(id);
    }
}
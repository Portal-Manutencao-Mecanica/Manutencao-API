package com.weg.Maintenance_API.designation.service;


import java.util.UUID;

import com.weg.Maintenance_API.enums.Sector;

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

    // Cria e persiste os dados da operacao.
    @Transactional
    public DesignationDtoResponse save(DesignationDtoRequest designationDtoRequest) {
        Designation designation = designationMapper.toEntity(designationDtoRequest);
        designation = designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<DesignationDtoResponse> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return designationRepository.findAll(pageable).map(designationMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public DesignationDtoResponse getById(UUID id) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DesignaÃƒÂ§ÃƒÂ£o", id));
        return designationMapper.toResponse(designation);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public DesignationDtoResponse update(UUID id, DesignationDtoRequest designationDtoRequest) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DesignaÃƒÂ§ÃƒÂ£o", id));
        designation.setSector(designationDtoRequest.sector());
        designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public DesignationDtoResponse patch(UUID id, DesignationPatchRequest request) {
        Designation designation = designationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DesignaÃƒÂ§ÃƒÂ£o", id));

        if (request.sector() != null) {
            designation.setSector(Sector.valueOf(
                    request.sector().trim().toUpperCase(java.util.Locale.ROOT)
            ));
        }

        designationRepository.save(designation);
        return designationMapper.toResponse(designation);
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        designationRepository.delete(designationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DesignaÃ§Ã£o", id)));
    }
}

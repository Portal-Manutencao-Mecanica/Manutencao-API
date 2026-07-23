package com.weg.Maintenance_API.maintenancerequest.service;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestPatchRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.entity.MaintenanceRequest;
import com.weg.Maintenance_API.maintenancerequest.mapper.MaintenanceRequestMapper;
import com.weg.Maintenance_API.maintenancerequest.repository.MaintenanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceRequestMapper maintenanceRequestMapper;

    @Transactional
    public MaintenanceRequestResponse save(MaintenanceRequestRequest request) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestMapper.toEntity(request);
        maintenanceRequest = maintenanceRepository.save(maintenanceRequest);
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRequestResponse> getAll() {
        return maintenanceRepository.findAll().stream().map(maintenanceRequestMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MaintenanceRequestResponse getById(Long id) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de manutenção", id));
        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    @Transactional
    public MaintenanceRequestResponse update(Long id, MaintenanceRequestRequest request) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de manutenção", id));
        maintenanceRequest.setSector(request.sector());
        maintenanceRequest.setPriority(request.priority());
        maintenanceRequest.setDescription(request.description());
        return maintenanceRequestMapper.toResponse(maintenanceRepository.save(maintenanceRequest));
    }

    @Transactional
    public MaintenanceRequestResponse patch(Long id, MaintenanceRequestPatchRequest request) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de manutenção", id));

        if (request.sector() != null) {
            maintenanceRequest.setSector(request.sector());
        }
        if (request.priority() != null) {
            maintenanceRequest.setPriority(request.priority());
        }
        if (request.description() != null) {
            maintenanceRequest.setDescription(request.description());
        }

        return maintenanceRequestMapper.toResponse(maintenanceRepository.save(maintenanceRequest));
    }

    @Transactional
    public void delete(Long id) {
        maintenanceRepository.deleteById(id);
    }
}
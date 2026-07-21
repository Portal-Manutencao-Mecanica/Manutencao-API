package com.weg.Maintenance_API.maintenancerequest.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public MaintenanceRequestResponse save(MaintenanceRequestRequest maintenanceRequestRequest) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestMapper.toEntity(maintenanceRequestRequest);

        maintenanceRequest = maintenanceRepository.save(maintenanceRequest);

        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    public List<MaintenanceRequestResponse> getAll() {
        List<MaintenanceRequest> maintenanceRequests = maintenanceRepository.findAll();

        return maintenanceRequests.stream().map(maintenanceRequestMapper::toResponse).toList();
    }

    public MaintenanceRequestResponse getById(Long id) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("")); // Falta a mensagem

        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    public MaintenanceRequestResponse update(Long id, MaintenanceRequestRequest maintenanceRequestRequest) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("")); // Falta a mensagem

        maintenanceRequest.setSector(maintenanceRequestRequest.sector());
        maintenanceRequest.setPriority(maintenanceRequestRequest.priority());
        maintenanceRequest.setDescription(maintenanceRequestRequest.description());

        maintenanceRequest = maintenanceRepository.save(maintenanceRequest);

        return maintenanceRequestMapper.toResponse(maintenanceRequest);
    }

    public void delete(Long id) {
        maintenanceRepository.deleteById(id);
    }
}

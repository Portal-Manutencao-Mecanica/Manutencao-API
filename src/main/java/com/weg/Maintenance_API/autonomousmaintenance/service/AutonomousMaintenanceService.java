package com.weg.Maintenance_API.autonomousmaintenance.service;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.autonomousmaintenance.mapper.AutonomousMaintenanceMapper;
import com.weg.Maintenance_API.autonomousmaintenance.repository.AutonomousMaintenanceRepository;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AutonomousMaintenanceService {

    private final AutonomousMaintenanceRepository repository;
    private final AutonomousMaintenanceMapper mapper;

    @Transactional
    public AutonomousMaintenanceDtoResponse create(AutonomousMaintenanceDtoRequest request) {
        AutonomousMaintenance entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public List<AutonomousMaintenanceDtoResponse> createAll(List<AutonomousMaintenanceDtoRequest> requests) {
        List<AutonomousMaintenance> entities = requests.stream()
                .map(mapper::toEntity)
                .toList();
        return repository.saveAll(entities).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AutonomousMaintenanceDtoResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutonomousMaintenanceDtoResponse getById(Long id) {
        AutonomousMaintenance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção autônoma", id));
        return mapper.toResponse(entity);
    }

    @Transactional
    public AutonomousMaintenanceDtoResponse update(Long id, AutonomousMaintenanceDtoRequest request) {
        AutonomousMaintenance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção autônoma", id));

        entity.setEquipmentSituation(request.equipmentSituation());
        entity.setInspectedAt(request.inspectedAt());
        entity.setEquipmentCondition(request.equipmentCondition());
        entity.setIdentifiedNonconformities(request.identifiedNonconformities());

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AutonomousMaintenanceDtoResponse> getBySituacao(String situacao) {
        EquipmentSituation equipmentSituation = EquipmentSituation.valueOf(
                situacao.trim().toUpperCase(Locale.ROOT)
        );
        return repository.findAllByEquipmentSituation(equipmentSituation).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
package com.weg.Maintenance_API.autonomousmaintenance.service;


import java.util.UUID;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.service.EntityReferenceService;

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
    private final EntityReferenceService references;

    // Cria e persiste os dados da operacao.
    @Transactional
    public AutonomousMaintenanceDtoResponse create(AutonomousMaintenanceDtoRequest request) {
        AutonomousMaintenance entity = mapper.toEntity(request);
        applyReferences(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    // Cria e persiste os dados da operacao.
    @Transactional
    public List<AutonomousMaintenanceDtoResponse> createAll(List<AutonomousMaintenanceDtoRequest> requests) {
        List<AutonomousMaintenance> entities = requests.stream()
                .map(this::toEntity)
                .toList();
        return repository.saveAll(entities).stream()
                .map(mapper::toResponse)
                .toList();
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<AutonomousMaintenanceDtoResponse> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public AutonomousMaintenanceDtoResponse getById(UUID id) {
        AutonomousMaintenance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManutenÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â£o autÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â´noma", id));
        return mapper.toResponse(entity);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public AutonomousMaintenanceDtoResponse update(UUID id, AutonomousMaintenanceDtoRequest request) {
        AutonomousMaintenance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManutenÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â§ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â£o autÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â´noma", id));

        entity.setEquipmentSituation(request.equipmentSituation());
        entity.setInspectedAt(request.inspectedAt());
        entity.setEquipmentCondition(EquipmentCondition.valueOf(request.equipmentCondition()));
        entity.setIdentifiedNonconformities(request.identifiedNonconformities());

        return mapper.toResponse(repository.save(entity));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        repository.delete(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ManutenÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â§ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â£o autÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â´noma", id)));
    }

    // Converte os dados para o formato necessario.
    private AutonomousMaintenance toEntity(AutonomousMaintenanceDtoRequest request) {
        AutonomousMaintenance entity = mapper.toEntity(request);
        applyReferences(entity, request);
        return entity;
    }

    // Aplica os dados recebidos na entidade.
    private void applyReferences(AutonomousMaintenance entity, AutonomousMaintenanceDtoRequest request) {
        entity.setInspectedMachine(references.machine(request.inspectedMachineId()));
        entity.setResponsibleTeacher(references.teacher(request.responsibleTeacherId()));
        entity.setResponsibleStudent(references.student(request.responsibleStudentId()));
    }
    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<AutonomousMaintenanceDtoResponse> getBySituacao(
            String situacao,
            org.springframework.data.domain.Pageable pageable
    ) {
        EquipmentSituation equipmentSituation = EquipmentSituation.valueOf(
                situacao.trim().toUpperCase(Locale.ROOT)
        );
        return repository.findAllByEquipmentSituation(equipmentSituation, pageable)
                .map(mapper::toResponse);
    }
}

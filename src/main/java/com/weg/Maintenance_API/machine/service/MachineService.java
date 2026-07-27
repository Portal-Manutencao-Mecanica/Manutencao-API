package com.weg.Maintenance_API.machine.service;


import java.util.UUID;
import java.util.Locale;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.machine.dto.request.MachinePatchRequest;
import com.weg.Maintenance_API.machine.dto.request.MachineRequest;
import com.weg.Maintenance_API.machine.dto.response.MachineResponse;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.machine.mapper.MachineMapper;
import com.weg.Maintenance_API.machine.repository.MachineRepository;
import com.weg.Maintenance_API.service.EntityReferenceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineMapper machineMapper;
    private final MachineRepository machineRepository;
    private final EntityReferenceService references;

    // Cria e persiste os dados da operacao.
    @Transactional
    public MachineResponse save(MachineRequest request) {
        Machine machine = machineMapper.toEntity(request);
        machine.setPlace(references.place(request.placeId()));
        machine = machineRepository.save(machine);
        return machineMapper.toResponse(machine);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MachineResponse> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return machineRepository.findAll(pageable).map(machineMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public MachineResponse getById(UUID id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("MÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡quina", id));
        return machineMapper.toResponse(machine);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public MachineResponse update(UUID id, MachineRequest request) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("MÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡quina", id));
        machine.setName(request.name());
        machine.setPatrimony(request.patrimony());
        machine.setCondition(EquipmentCondition.valueOf(request.condition()));
        machine.setTag(request.tag());
        machine.setPlace(references.place(request.placeId()));
        return machineMapper.toResponse(machineRepository.save(machine));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public MachineResponse patch(UUID id, MachinePatchRequest request) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("MÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¡quina", id));

        if (request.name() != null) {
            machine.setName(request.name());
        }
        if (request.patrimony() != null) {
            machine.setPatrimony(request.patrimony());
        }
        if (request.condition() != null) {
            machine.setCondition(EquipmentCondition.valueOf(
                    request.condition().trim().toUpperCase(Locale.ROOT)
            ));
        }
        if (request.tag() != null) {
            machine.setTag(request.tag());
        }

        return machineMapper.toResponse(machineRepository.save(machine));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        machineRepository.delete(machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("MÃƒÆ’Ã‚Â¡quina", id)));
    }
}

package com.weg.Maintenance_API.machine.service;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.machine.dto.request.MachinePatchRequest;
import com.weg.Maintenance_API.machine.dto.request.MachineRequest;
import com.weg.Maintenance_API.machine.dto.response.MachineResponse;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.machine.mapper.MachineMapper;
import com.weg.Maintenance_API.machine.repository.MachineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineMapper machineMapper;
    private final MachineRepository machineRepository;

    @Transactional
    public MachineResponse save(MachineRequest request) {
        Machine machine = machineMapper.toEntity(request);
        machine = machineRepository.save(machine);
        return machineMapper.toResponse(machine);
    }

    @Transactional(readOnly = true)
    public List<MachineResponse> getAll() {
        return machineRepository.findAll().stream().map(machineMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MachineResponse getById(Long id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Máquina", id));
        return machineMapper.toResponse(machine);
    }

    @Transactional
    public MachineResponse update(Long id, MachineRequest request) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Máquina", id));
        machine.setName(request.name());
        machine.setPatrimony(request.patrimony());
        machine.setCondition(request.condition());
        machine.setTag(request.tag());
        return machineMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public MachineResponse patch(Long id, MachinePatchRequest request) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Máquina", id));

        if (request.name() != null) {
            machine.setName(request.name());
        }
        if (request.patrimony() != null) {
            machine.setPatrimony(request.patrimony());
        }
        if (request.condition() != null) {
            machine.setCondition(request.condition());
        }
        if (request.tag() != null) {
            machine.setTag(request.tag());
        }

        return machineMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public void delete(Long id) {
        machineRepository.deleteById(id);
    }
}
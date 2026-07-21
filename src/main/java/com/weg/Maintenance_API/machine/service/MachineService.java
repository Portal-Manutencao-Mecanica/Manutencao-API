package com.weg.Maintenance_API.machine.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public MachineResponse save(MachineRequest machineRequest) {
        Machine machine = machineMapper.toEntity(machineRequest);

        machine = machineRepository.save(machine);

        return machineMapper.toResponse(machine);
    }

    @Transactional(readOnly = true)
    public List<MachineResponse> getAll() {
        List<Machine> machines = machineRepository.findAll();

        return machines.stream().map(machineMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MachineResponse getById(Long id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        return machineMapper.toResponse(machine);
    }

    @Transactional
    public MachineResponse update(Long id, MachineRequest machineRequest) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        machine.setName(machineRequest.name());
        machine.setPatrimony(machineRequest.patrimony());
        machine.setCondition(machineRequest.condition());
        machine.setTag(machineRequest.tag());

        machine = machineRepository.save(machine);

        return machineMapper.toResponse(machine);
    }

    @Transactional
    public void delete(Long id) {
        machineRepository.deleteById(id);
    }
}

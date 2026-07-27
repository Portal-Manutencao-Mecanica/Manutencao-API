package com.weg.Maintenance_API.equipment.service;


import java.util.UUID;

import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.Maintenance_API.equipment.dto.request.EquipmentPatchRequest;
import com.weg.Maintenance_API.equipment.dto.request.EquipmentRequest;
import com.weg.Maintenance_API.equipment.dto.response.EquipmentResponse;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.equipment.mapper.EquipmentMapper;
import com.weg.Maintenance_API.equipment.repository.EquipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public EquipmentResponse save(EquipmentRequest equipmentRequest) {
        Equipment equipment = equipmentMapper.toEntity(equipmentRequest);
        equipment = equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAll() {
        return equipmentRepository.findAll().stream().map(equipmentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getById(UUID id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional
    public EquipmentResponse update(UUID id, EquipmentRequest equipmentRequest) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));
        equipment.setName(equipmentRequest.name());
        equipment.setSap(equipmentRequest.sap());
        equipment.setUnitPrice(equipmentRequest.unitPrice());
        equipment.setAvailableQuantity(equipmentRequest.availableQuantity());
        equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional
    public EquipmentResponse patch(UUID id, EquipmentPatchRequest request) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));

        if (request.name() != null) {
            equipment.setName(request.name());
        }
        if (request.sap() != null) {
            equipment.setSap(request.sap());
        }
        if (request.unitPrice() != null) {
            equipment.setUnitPrice(request.unitPrice());
        }
        if (request.availableQuantity() != null) {
            equipment.setAvailableQuantity(request.availableQuantity());
        }

        equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional
    public void delete(UUID id) {
        equipmentRepository.deleteById(id);
    }
}
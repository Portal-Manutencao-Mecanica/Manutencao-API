package com.weg.Maintenance_API.equipment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<Equipment> equipments = equipmentRepository.findAll();

        return equipments.stream().map(equipmentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getById(Long id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new RuntimeException(""));

        return equipmentMapper.toResponse(equipment);
    }

    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest equipmentRequest) {
        if (!equipmentRepository.existsById(id)) {
            throw new RuntimeException("");
        }

        Equipment equipment = equipmentMapper.toEntity(equipmentRequest);

        equipment.setId(id);
        equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(equipment);
    }

    @Transactional
    public void delete(Long id) {
        equipmentRepository.deleteById(id);
    }
}

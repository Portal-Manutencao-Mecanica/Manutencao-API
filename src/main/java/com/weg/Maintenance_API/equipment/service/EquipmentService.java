package com.weg.Maintenance_API.equipment.service;


import java.util.UUID;
import java.util.Locale;

import com.weg.Maintenance_API.enums.MediaType;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.media.service.ImageMediaFactory;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.service.AuthenticatedUserService;

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
    private final AuthenticatedUserService authenticatedUserService;
    private final ImageMediaFactory imageMediaFactory;

    // Cria e persiste os dados da operacao.
    @Transactional
    public EquipmentResponse save(EquipmentRequest equipmentRequest) {
        Equipment equipment = equipmentMapper.toEntity(equipmentRequest);
        assignAutomaticIdentifiers(equipment);
        replaceImageWhenProvided(equipment, equipmentRequest.image());
        equipment = equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(equipment);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<EquipmentResponse> getAll(
            String search,
            org.springframework.data.domain.Pageable pageable
    ) {
        String normalizedSearch = search == null || search.isBlank()
                ? ""
                : search.trim();
        return equipmentRepository.findAllFiltered(normalizedSearch, pageable)
                .map(equipmentMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public EquipmentResponse getById(UUID id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));
        return equipmentMapper.toResponse(equipment);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public EquipmentResponse update(UUID id, EquipmentRequest equipmentRequest) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));
        equipment.setName(equipmentRequest.name());
        equipment.setUnitPrice(equipmentRequest.unitPrice());
        equipment.setAvailableQuantity(equipmentRequest.availableQuantity());
        ensureAutomaticIdentifiers(equipment);
        replaceImageWhenProvided(equipment, equipmentRequest.image());
        equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(equipment);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public EquipmentResponse patch(UUID id, EquipmentPatchRequest request) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id));

        if (request.name() != null) {
            equipment.setName(request.name());
        }
        if (request.unitPrice() != null) {
            equipment.setUnitPrice(request.unitPrice());
        }
        if (request.availableQuantity() != null) {
            equipment.setAvailableQuantity(request.availableQuantity());
        }
        ensureAutomaticIdentifiers(equipment);

        replaceImageWhenProvided(equipment, request.image());
        equipmentRepository.save(equipment);
        return equipmentMapper.toResponse(equipment);
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        equipmentRepository.delete(equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipamento", id)));
    }

    private void replaceImageWhenProvided(Equipment equipment, String image) {
        if (image == null) {
            return;
        }
        User currentUser = authenticatedUserService.requireCurrentUser();
        equipment.getMedia().clear();
        equipment.getMedia().addAll(imageMediaFactory.fromDataUrls(
                List.of(image),
                currentUser,
                MediaType.EQUIPMENT,
                "equipamento",
                "Imagem do equipamento",
                false
        ));
    }
    private void assignAutomaticIdentifiers(Equipment equipment) {
        String suffix = identifierSuffix();
        equipment.setSap("SAP-" + suffix);
        equipment.setPatrimony("PAT-" + suffix);
        equipment.setTag("TAG-" + suffix);
    }

    private void ensureAutomaticIdentifiers(Equipment equipment) {
        if (hasText(equipment.getSap())
                && hasText(equipment.getPatrimony())
                && hasText(equipment.getTag())) {
            return;
        }

        String suffix = identifierSuffix();
        if (!hasText(equipment.getSap())) equipment.setSap("SAP-" + suffix);
        if (!hasText(equipment.getPatrimony())) equipment.setPatrimony("PAT-" + suffix);
        if (!hasText(equipment.getTag())) equipment.setTag("TAG-" + suffix);
    }

    private String identifierSuffix() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

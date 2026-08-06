package com.weg.Maintenance_API.machine.service;


import java.util.UUID;
import java.util.Locale;

import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.exception.type.InvalidFileException;

import java.util.List;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final int MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    private static final Pattern IMAGE_DATA_URL = Pattern.compile(
            "^data:(image/(?:png|jpeg|webp|svg\\+xml));base64,([A-Za-z0-9+/=]+)$"
    );

    private final MachineMapper machineMapper;
    private final MachineRepository machineRepository;
    private final EntityReferenceService references;

    // Cria e persiste os dados da operacao.
    @Transactional
    public MachineResponse save(MachineRequest request) {
        Machine machine = machineMapper.toEntity(request);
        machine.setPlace(references.place(request.placeId()));
        machine.setImage(normalizeImage(request.image()));
        machine = machineRepository.save(machine);
        return machineMapper.toResponse(machine);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MachineResponse> getAll(
            String search,
            EquipmentCondition condition,
            org.springframework.data.domain.Pageable pageable
    ) {
        String normalizedSearch = search == null || search.isBlank()
                ? ""
                : search.trim();
        return machineRepository.findAllFiltered(
                normalizedSearch,
                condition,
                pageable
        ).map(machineMapper::toResponse);
    }

    // Busca os dados necessarios para esta operacao.
    @Transactional(readOnly = true)
    public MachineResponse getById(UUID id) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Máquina", id));
        return machineMapper.toResponse(machine);
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public MachineResponse update(UUID id, MachineRequest request) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Máquina", id));
        machine.setName(request.name());
        machine.setPatrimony(request.patrimony());
        machine.setCondition(EquipmentCondition.valueOf(request.condition()));
        machine.setTag(request.tag());
        machine.setPlace(references.place(request.placeId()));
        machine.setImage(normalizeImage(request.image()));
        return machineMapper.toResponse(machineRepository.save(machine));
    }

    // Atualiza o estado conforme os dados informados.
    @Transactional
    public MachineResponse patch(UUID id, MachinePatchRequest request) {
        Machine machine = machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Máquina", id));

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
        if (request.image() != null) {
            machine.setImage(normalizeImage(request.image()));
        }

        return machineMapper.toResponse(machineRepository.save(machine));
    }

    // Remove ou invalida os dados solicitados.
    @Transactional
    public void delete(UUID id) {
        machineRepository.delete(machineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Máquina", id)));
    }

    private String normalizeImage(String image) {
        if (image == null || image.isBlank()) {
            return null;
        }
        Matcher matcher = IMAGE_DATA_URL.matcher(image);
        if (!matcher.matches()) {
            throw new InvalidFileException("A imagem da máquina possui formato inválido.");
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(matcher.group(2));
        } catch (IllegalArgumentException exception) {
            throw new InvalidFileException("A imagem da máquina possui Base64 inválido.", exception);
        }
        if (bytes.length == 0 || bytes.length > MAX_IMAGE_BYTES) {
            throw new InvalidFileException("A imagem da máquina deve ter no máximo 5 MB.");
        }
        return image;
    }
}

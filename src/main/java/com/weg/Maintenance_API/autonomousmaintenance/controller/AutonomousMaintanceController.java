package com.weg.Maintenance_API.autonomousmaintenance.controller;

import org.springframework.security.access.prepost.PreAuthorize;


import java.util.UUID;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.autonomousmaintenance.service.AutonomousMaintenanceService;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@RequestMapping("/manutencao-autonoma")
public class AutonomousMaintanceController {

    private final AutonomousMaintenanceService service;

    // Cria e persiste os dados da operacao.
    @PostMapping
    public ResponseEntity<AutonomousMaintenanceDtoResponse> create(
            @Valid @RequestBody AutonomousMaintenanceDtoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    // Cria e persiste os dados da operacao.
    @PostMapping("/create-all")
    public ResponseEntity<List<AutonomousMaintenanceDtoResponse>> createAll(
            @Valid @RequestBody List<AutonomousMaintenanceDtoRequest> requests
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAll(requests));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<AutonomousMaintenanceDtoResponse>> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> getById(
            @PathVariable
UUID id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @PutMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody AutonomousMaintenanceDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // Remove ou invalida os dados solicitados.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/situacao/{situacao}")
    public ResponseEntity<org.springframework.data.domain.Page<AutonomousMaintenanceDtoResponse>> getBySituacao(
            @PathVariable
            @ValidEnum(message = "A situacao informada e invalida.", enumClass = EquipmentSituation.class) String situacao,
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getBySituacao(situacao, pageable));
    }
}

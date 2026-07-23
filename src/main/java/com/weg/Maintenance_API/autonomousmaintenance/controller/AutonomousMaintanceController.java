package com.weg.Maintenance_API.autonomousmaintenance.controller;


import java.util.UUID;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.autonomousmaintenance.service.AutonomousMaintenanceService;
import com.weg.Maintenance_API.enums.EquipmentSituation;
import com.weg.Maintenance_API.validation.EntityExists;
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
@RequiredArgsConstructor
@RequestMapping("/manutencao-autonoma")
public class AutonomousMaintanceController {

    private final AutonomousMaintenanceService service;

    @PostMapping
    public ResponseEntity<AutonomousMaintenanceDtoResponse> create(
            @Valid @RequestBody AutonomousMaintenanceDtoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PostMapping("/create-all")
    public ResponseEntity<List<AutonomousMaintenanceDtoResponse>> createAll(
            @Valid @RequestBody List<AutonomousMaintenanceDtoRequest> requests
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAll(requests));
    }

    @GetMapping
    public ResponseEntity<List<AutonomousMaintenanceDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> getById(
            @PathVariable
            @EntityExists(message = "entity not found", entityClass = AutonomousMaintenance.class) UUID id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody AutonomousMaintenanceDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/situacao/{situacao}")
    public ResponseEntity<List<AutonomousMaintenanceDtoResponse>> getBySituacao(
            @PathVariable
            @ValidEnum(message = "enum is invalid", enumClass = EquipmentSituation.class) String situacao
    ) {
        return ResponseEntity.ok(service.getBySituacao(situacao));
    }
}
package com.weg.Maintenance_API.equipment.controller;


import java.util.UUID;

import com.weg.Maintenance_API.equipment.dto.request.EquipmentPatchRequest;
import com.weg.Maintenance_API.equipment.dto.request.EquipmentRequest;
import com.weg.Maintenance_API.equipment.dto.response.EquipmentResponse;
import com.weg.Maintenance_API.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipamento")
public class EquipmentController {

    private final EquipmentService service;

    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@Valid @RequestBody EquipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody EquipmentRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EquipmentResponse> patch(
            @PathVariable UUID id,
            @RequestBody EquipmentPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
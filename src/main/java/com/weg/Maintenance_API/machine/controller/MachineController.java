package com.weg.Maintenance_API.machine.controller;

import org.springframework.data.domain.Pageable;


import java.util.UUID;

import com.weg.Maintenance_API.machine.dto.request.MachinePatchRequest;
import com.weg.Maintenance_API.machine.dto.request.MachineRequest;
import com.weg.Maintenance_API.machine.dto.response.MachineResponse;
import com.weg.Maintenance_API.machine.service.MachineService;
import com.weg.Maintenance_API.enums.EquipmentCondition;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maquinas")
public class MachineController {

    private final MachineService service;

    // Cria e persiste os dados da operacao.
    @PostMapping
    public ResponseEntity<MachineResponse> create(@Valid @RequestBody MachineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public ResponseEntity<Page<MachineResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EquipmentCondition condition,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(search, condition, pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public ResponseEntity<MachineResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @PutMapping("/{id}")
    public ResponseEntity<MachineResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody MachineRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}")
    public ResponseEntity<MachineResponse> patch(
            @PathVariable UUID id,
            @RequestBody MachinePatchRequest request) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    // Remove ou invalida os dados solicitados.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

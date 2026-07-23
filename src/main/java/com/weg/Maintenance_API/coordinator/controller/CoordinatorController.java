package com.weg.Maintenance_API.coordinator.controller;


import java.util.UUID;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorPatchRequest;
import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.coordinator.service.CoordinatorService;
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
@RequestMapping({"/coordenador", "/coordernador"})
public class CoordinatorController {

    private final CoordinatorService service;

    @PostMapping
    public ResponseEntity<CoordinatorResponseDto> create(@Valid @RequestBody CoordinatorRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CoordinatorResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<CoordinatorResponseDto>> getAllAtivos() {
        return ResponseEntity.ok(service.getAllAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CoordinatorRequestDto request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> patch(
            @PathVariable UUID id,
            @RequestBody CoordinatorPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<CoordinatorResponseDto> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.inativar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
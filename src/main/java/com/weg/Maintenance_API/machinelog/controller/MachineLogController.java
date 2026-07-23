package com.weg.Maintenance_API.machinelog.controller;


import java.util.UUID;

import com.weg.Maintenance_API.machinelog.dto.request.MachineLogPatchRequest;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.machinelog.service.MachineLogService;
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
@RequestMapping("/maquina-log")
public class MachineLogController {

    private final MachineLogService service;

    @PostMapping
    public ResponseEntity<MachineLogResponse> create(@Valid @RequestBody MachineLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<MachineLogResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineLogResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MachineLogResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody MachineLogRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MachineLogResponse> patch(
            @PathVariable UUID id,
            @RequestBody MachineLogPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
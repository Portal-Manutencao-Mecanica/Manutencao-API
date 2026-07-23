package com.weg.Maintenance_API.maintenancerequest.controller;

import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestPatchRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.service.MaintenanceRequestService;
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
@RequestMapping("/solicitao-manutencao")
public class MaintanceRequestController {

    private final MaintenanceRequestService service;

    @PostMapping
    public ResponseEntity<MaintenanceRequestResponse> create(
            @Valid @RequestBody MaintenanceRequestRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceRequestResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequestRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> patch(
            @PathVariable Long id,
            @RequestBody MaintenanceRequestPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
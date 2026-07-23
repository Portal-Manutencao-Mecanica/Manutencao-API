package com.weg.Maintenance_API.designation.controller;


import java.util.UUID;

import com.weg.Maintenance_API.designation.dto.request.DesignationDtoRequest;
import com.weg.Maintenance_API.designation.dto.request.DesignationPatchRequest;
import com.weg.Maintenance_API.designation.dto.response.DesignationDtoResponse;
import com.weg.Maintenance_API.designation.service.DesignationService;
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
@RequestMapping("/designacao")
public class DesignationController {

    private final DesignationService service;

    @PostMapping
    public ResponseEntity<DesignationDtoResponse> create(@Valid @RequestBody DesignationDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<DesignationDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignationDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignationDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DesignationDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DesignationDtoResponse> patch(
            @PathVariable UUID id,
            @RequestBody DesignationPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
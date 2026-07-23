package com.weg.Maintenance_API.buy.controller;

import org.springframework.security.access.prepost.PreAuthorize;


import java.util.UUID;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.request.BuyPatchRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.buy.service.BuyService;
import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.validation.EntityExists;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
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
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@RequestMapping("/compras")
public class BuyController {

    private final BuyService service;

    @PostMapping
    public ResponseEntity<BuyDtoResponse> create(@Valid @RequestBody BuyDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<BuyDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> getById(
            @PathVariable
            @EntityExists(message = "entity not found", entityClass = Buy.class) UUID id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody BuyDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> patch(
            @PathVariable UUID id,
            @RequestBody BuyPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BuyDtoResponse>> getByStatus(
            @PathVariable
            @ValidEnum(message = "enum is invalid", enumClass = BuyStatus.class) String status
    ) {
        return ResponseEntity.ok(service.getByStatus(status));
    }
}
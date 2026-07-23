package com.weg.Maintenance_API.helpermaterial.controller;

import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialPatchRequest;
import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialRequest;
import com.weg.Maintenance_API.helpermaterial.dto.response.HelperMaterialResponse;
import com.weg.Maintenance_API.helpermaterial.service.HelperMaterialService;
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
@RequestMapping("/material-apoio")
public class HelperMaterialController {

    private final HelperMaterialService service;

    @PostMapping
    public ResponseEntity<HelperMaterialResponse> create(@Valid @RequestBody HelperMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<HelperMaterialResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HelperMaterialRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> patch(
            @PathVariable Long id,
            @RequestBody HelperMaterialPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
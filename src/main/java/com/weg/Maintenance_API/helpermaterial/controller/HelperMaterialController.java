package com.weg.Maintenance_API.helpermaterial.controller;



import java.util.UUID;

import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialPatchRequest;
import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialRequest;
import com.weg.Maintenance_API.helpermaterial.dto.response.HelperMaterialResponse;
import com.weg.Maintenance_API.helpermaterial.service.HelperMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // Cria e persiste os dados da operacao.
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<HelperMaterialResponse> create(@Valid @RequestBody HelperMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    // Busca os dados necessarios para esta operacao.
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<HelperMaterialResponse>> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody HelperMaterialRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // Atualiza o estado conforme os dados informados.
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> patch(
            @PathVariable UUID id,
            @RequestBody HelperMaterialPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    // Remove ou invalida os dados solicitados.
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

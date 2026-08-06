package com.weg.Maintenance_API.autonomousmaintenance.controller;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceApprovalRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Maintenance_API.autonomousmaintenance.service.AutonomousMaintenanceService;
import com.weg.Maintenance_API.enums.AutonomousMaintenanceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manutencao-autonoma")
@Tag(name = "Manutencao autonoma")
public class AutonomousMaintanceController {

    private final AutonomousMaintenanceService service;

    @Operation(summary = "Cria uma manutencao autonoma pendente de aprovacao")
    @ApiResponse(responseCode = "201", description = "Manutencao criada")
    @ApiResponse(responseCode = "403", description = "Usuario nao e professor")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<AutonomousMaintenanceDtoResponse> create(
            @Valid @RequestBody AutonomousMaintenanceDtoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Lista manutencoes visiveis para o usuario autenticado")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ALUNO', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AutonomousMaintenanceDtoResponse>> getAll(
            @RequestParam(required = false) AutonomousMaintenanceStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(status, pageable));
    }

    @Operation(summary = "Detalha uma manutencao respeitando propriedade, organizacao e atribuicao")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ALUNO', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Altera uma manutencao ainda pendente")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody AutonomousMaintenanceDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Aprova ou reprova uma manutencao autonoma pendente")
    @ApiResponse(responseCode = "200", description = "Decisao registrada")
    @ApiResponse(responseCode = "400", description = "Reprovacao sem motivo")
    @ApiResponse(responseCode = "403", description = "Coordenador de outra organizacao")
    @ApiResponse(responseCode = "422", description = "Manutencao ja decidida")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @PatchMapping("/{id}/aprovacao")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> decide(
            @PathVariable UUID id,
            @Valid @RequestBody AutonomousMaintenanceApprovalRequest request
    ) {
        return ResponseEntity.ok(service.decide(id, request));
    }

    @Operation(summary = "Cancela uma manutencao ainda pendente")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

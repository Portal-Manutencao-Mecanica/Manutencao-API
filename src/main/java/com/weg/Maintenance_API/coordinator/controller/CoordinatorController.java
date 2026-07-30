package com.weg.Maintenance_API.coordinator.controller;



import java.util.UUID;

import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.coordinator.service.CoordinatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/coordenador", "/coordernador"})
public class CoordinatorController {

    private final CoordinatorService service;

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CoordinatorResponseDto>> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/ativos")
    public ResponseEntity<org.springframework.data.domain.Page<CoordinatorResponseDto>> getAllAtivos(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllAtivos(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

}

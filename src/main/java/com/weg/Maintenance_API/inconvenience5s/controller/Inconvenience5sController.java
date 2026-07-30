package com.weg.Maintenance_API.inconvenience5s.controller;



import java.util.UUID;

import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SPatchRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.response.Inconvenience5SDtoResponse;
import com.weg.Maintenance_API.inconvenience5s.service.Inconvenience5sService;
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
@RequestMapping("/5s")
public class Inconvenience5sController {

    private final Inconvenience5sService service;

    // Cria e persiste os dados da operacao.
    @PostMapping
    public ResponseEntity<Inconvenience5SDtoResponse> create(@Valid @RequestBody Inconvenience5SDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<Inconvenience5SDtoResponse>> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @PutMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody Inconvenience5SDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> patch(
            @PathVariable UUID id,
            @RequestBody Inconvenience5SPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    // Remove ou invalida os dados solicitados.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

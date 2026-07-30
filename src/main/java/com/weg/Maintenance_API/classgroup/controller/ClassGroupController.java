package com.weg.Maintenance_API.classgroup.controller;

import com.weg.Maintenance_API.classgroup.entity.ClassGroup;


import java.util.UUID;

import com.weg.Maintenance_API.classgroup.dto.request.ClassPatchRequest;
import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;
import com.weg.Maintenance_API.classgroup.service.ClassGroupService;
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

@RequestMapping("/turma")
@RestController
@RequiredArgsConstructor
public class ClassGroupController {

    private final ClassGroupService service;

    // Cria e persiste os dados da operacao.
    @PostMapping
    public ResponseEntity<ClassResponseDto> create(@Valid @RequestBody ClassRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<ClassResponseDto>> getAll(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/ativos")
    public ResponseEntity<org.springframework.data.domain.Page<ClassResponseDto>> getAllAtivos(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllAtivos(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @PutMapping("/{id}")
    public ResponseEntity<ClassResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClassRequestDto request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}")
    public ResponseEntity<ClassResponseDto> patch(
            @PathVariable UUID id,
            @RequestBody ClassPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    // Executa a operacao deste metodo.
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<ClassResponseDto> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.inativar(id));
    }

    // Remove ou invalida os dados solicitados.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable
UUID id
    ) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

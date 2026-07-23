package com.weg.Maintenance_API.classgroup.controller;

import com.weg.Maintenance_API.classgroup.dto.request.ClassPatchRequest;
import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;
import com.weg.Maintenance_API.classgroup.service.ClassGroupService;
import com.weg.Maintenance_API.validation.EntityExists;
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

    @PostMapping
    public ResponseEntity<ClassResponseDto> create(@Valid @RequestBody ClassRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ClassResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<ClassResponseDto>> getAllAtivos() {
        return ResponseEntity.ok(service.getAllAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ClassRequestDto request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClassResponseDto> patch(
            @PathVariable Long id,
            @RequestBody ClassPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<ClassResponseDto> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.inativar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable
            @EntityExists(
                    message = "entity is null",
                    entityClass = com.weg.Maintenance_API.classgroup.entity.ClassGroup.class
            ) Long id
    ) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
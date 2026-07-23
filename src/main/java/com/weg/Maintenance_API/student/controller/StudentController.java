package com.weg.Maintenance_API.student.controller;

import org.springframework.security.access.prepost.PreAuthorize;


import java.util.UUID;

import com.weg.Maintenance_API.student.dto.request.StudentDtoRequest;
import com.weg.Maintenance_API.student.dto.request.StudentPatchRequest;
import com.weg.Maintenance_API.student.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.student.service.StudentService;
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
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/alunos")
public class StudentController {

    private final StudentService service;

    @PostMapping
    @PreAuthorize("denyAll()")
    public ResponseEntity<StudentDtoResponse> create(@Valid @RequestBody StudentDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<StudentDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<StudentDtoResponse>> getAllAtivos() {
        return ResponseEntity.ok(service.getAllAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StudentDtoResponse> patch(
            @PathVariable UUID id,
            @RequestBody StudentPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<StudentDtoResponse> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.inativar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

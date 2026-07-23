package com.weg.Maintenance_API.teacher.controller;


import java.util.UUID;

import com.weg.Maintenance_API.teacher.dto.request.TeacherPatchRequest;
import com.weg.Maintenance_API.teacher.dto.request.TeacherRequestDto;
import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;
import com.weg.Maintenance_API.teacher.service.TeacherService;
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
@RequestMapping("/professores")
public class TeacherController {

    private final TeacherService service;

    @PostMapping
    public ResponseEntity<TeacherResponseDto> create(@Valid @RequestBody TeacherRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<TeacherResponseDto>> getAllAtivos() {
        return ResponseEntity.ok(service.getAllAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody TeacherRequestDto request
    ) {
        return ResponseEntity.ok(service.update(request, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> patch(
            @PathVariable UUID id,
            @RequestBody TeacherPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<TeacherResponseDto> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.inativar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
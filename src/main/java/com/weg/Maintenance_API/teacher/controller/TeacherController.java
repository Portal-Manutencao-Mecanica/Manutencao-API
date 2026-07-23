package com.weg.Maintenance_API.teacher.controller;

import org.springframework.security.access.prepost.PreAuthorize;


import java.util.UUID;

import com.weg.Maintenance_API.teacher.dto.response.TeacherResponseDto;
import com.weg.Maintenance_API.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/professores")
public class TeacherController {

    private final TeacherService service;

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

}

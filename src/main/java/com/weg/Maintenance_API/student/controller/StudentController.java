package com.weg.Maintenance_API.student.controller;



import java.util.UUID;

import com.weg.Maintenance_API.student.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alunos")
public class StudentController {

    private final StudentService service;

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<StudentDtoResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean enabled,
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(search, enabled, pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/ativos")
    public ResponseEntity<org.springframework.data.domain.Page<StudentDtoResponse>> getAllAtivos(
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllAtivos(pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public ResponseEntity<StudentDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

}

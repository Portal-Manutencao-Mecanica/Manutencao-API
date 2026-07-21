package com.weg.Maintenance_API.coordinator.controller;

import com.weg.Maintenance_API.coordinator.service.CoordinatorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coordernador")
public class CoordinatorController {
    private final CoordinatorService service;

    // Cria um coordenador, só o admin ira conseguir criar um coordenador
    @PostMapping()
    public ResponseEntity<CoordinatorResponseDto> create(@Valid @RequestBody CoordinatorRequestDto request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<CoordinatorResponseDto>> getAll() {
        return new ResponseEntity<>(service.getAll(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(service.getById(id),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> update(@PathVariable Long id,
            @Valid @RequestBody CoordinatorRequestDto entity) {
        return new ResponseEntity<>(service.update(id,entity),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

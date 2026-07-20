package com.weg.Maintenance_API.coordinator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;

import io.micrometer.core.ipc.http.HttpSender.Response;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coordernador")
public class CoordinatorController {

    // Cria um coordenador, só o admin ira conseguir criar um coordenador
    @PostMapping()
    public ResponseEntity<CoordinatorResponseDto> create(@RequestBody CoordinatorRequestDto entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<CoordinatorResponseDto> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordinatorResponseDto> update(@PathVariable Long id,
            @RequestBody CoordinatorRequestDto entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

}

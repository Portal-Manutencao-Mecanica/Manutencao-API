package com.weg.Maintenance_API.inconvenience5s.controller;

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

    @PostMapping
    public ResponseEntity<Inconvenience5SDtoResponse> create(@Valid @RequestBody Inconvenience5SDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public ResponseEntity<List<Inconvenience5SDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody Inconvenience5SDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> patch(
            @PathVariable Long id,
            @RequestBody Inconvenience5SPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
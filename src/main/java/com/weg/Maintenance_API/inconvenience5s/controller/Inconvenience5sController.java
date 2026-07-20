package com.weg.Maintenance_API.inconvenience5s.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.helpermaterial.dto.request.HelperMaterialRequest;
import com.weg.Maintenance_API.helpermaterial.dto.response.HelperMaterialResponse;
import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.response.Inconvenience5SDtoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/5s")
@RequiredArgsConstructor
public class Inconvenience5sController {

    @PostMapping()
    public ResponseEntity<Inconvenience5SDtoResponse> create(@RequestBody Inconvenience5SDtoRequest entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<Inconvenience5SDtoResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inconvenience5SDtoResponse> update(@PathVariable Long id,
            @RequestBody Inconvenience5SDtoRequest entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

}

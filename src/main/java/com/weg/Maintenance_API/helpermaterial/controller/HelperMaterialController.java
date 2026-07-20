package com.weg.Maintenance_API.helpermaterial.controller;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/material-apoio")
@RequiredArgsConstructor
@RestController
public class HelperMaterialController {

    @PostMapping()
    public ResponseEntity<HelperMaterialResponse> create(@Valid @RequestBody HelperMaterialResponse entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<HelperMaterialResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<HelperMaterialResponse> update(@PathVariable Long id,
            @Valid @RequestBody HelperMaterialRequest entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/tipo/{tipo}")
    public String getMethodName(@PathVariable String tipo) {
        return null;
    }

}

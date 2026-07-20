package com.weg.Maintenance_API.equipment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.buy.dto.response.EquipmentDtoResponse;
import com.weg.Maintenance_API.coordinator.dto.request.CoordinatorRequestDto;
import com.weg.Maintenance_API.coordinator.dto.response.CoordinatorResponseDto;
import com.weg.Maintenance_API.equipment.dto.request.EquipmentRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/equipamento")
@RequiredArgsConstructor
public class EquipmentController {

    // Cria um Equipamento
    @PostMapping()
    public ResponseEntity<EquipmentDtoResponse> create(@RequestBody EquipmentRequest entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<EquipmentDtoResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDtoResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDtoResponse> update(@PathVariable Long id,
            @RequestBody EquipmentRequest entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

}

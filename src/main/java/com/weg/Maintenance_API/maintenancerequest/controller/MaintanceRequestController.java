package com.weg.Maintenance_API.maintenancerequest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/solicitao-manutencao")
@RequiredArgsConstructor
@RestController
public class MaintanceRequestController {

    @PostMapping()
    public ResponseEntity<MaintenanceRequestResponse> create(@RequestBody MaintenanceRequestRequest entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<MaintenanceRequestResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> update(@PathVariable Long id,
            @RequestBody MaintenanceRequestRequest entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

}

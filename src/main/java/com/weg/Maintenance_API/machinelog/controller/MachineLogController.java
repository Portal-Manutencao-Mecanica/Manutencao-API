package com.weg.Maintenance_API.machinelog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.machine.dto.request.MachineRequest;
import com.weg.Maintenance_API.machine.dto.response.MachineResponse;
import com.weg.Maintenance_API.machinelog.dto.request.MachineLogRequest;
import com.weg.Maintenance_API.machinelog.dto.response.MachineLogResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/maquina-log")
@RequiredArgsConstructor
public class MachineLogController {

    @PostMapping()
    public ResponseEntity<MachineLogResponse> create(@RequestBody MachineLogRequest entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<MachineLogResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineLogResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<MachineLogResponse> update(@PathVariable Long id,
            @RequestBody MachineLogRequest entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

}

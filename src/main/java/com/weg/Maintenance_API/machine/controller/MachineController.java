package com.weg.Maintenance_API.machine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.inconvenience5s.dto.requests.Inconvenience5SDtoRequest;
import com.weg.Maintenance_API.inconvenience5s.dto.response.Inconvenience5SDtoResponse;
import com.weg.Maintenance_API.machine.dto.request.MachineRequest;
import com.weg.Maintenance_API.machine.dto.response.MachineResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/maquina")
@RequiredArgsConstructor
public class MachineController {

        @PostMapping()
    public ResponseEntity<MachineResponse> create(@RequestBody MachineRequest entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<MachineResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<MachineResponse> update(@PathVariable Long id,
            @RequestBody MachineRequest entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

}

package com.weg.Maintenance_API.autonomousmaintenance.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.autonomousmaintenance.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Maintenance_API.autonomousmaintenance.dto.response.AutonomousMaintenanceDtoResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("/manutencao-autonoma")
@RestController
@RequiredArgsConstructor
public class AutonomousMaintanceController {

    // Cria uma manutneção autonoma
    @PostMapping()
    public ResponseEntity<AutonomousMaintenanceDtoResponse> create(
            @RequestBody AutonomousMaintenanceDtoRequest entity) {
        return null;
    }

    // Utiliza uma lista de entidades e cadastra no banco de dados
    @PostMapping("/create-all")
    public ResponseEntity<List<AutonomousMaintenanceDtoResponse>> createAll(
            @RequestBody List<AutonomousMaintenanceDtoRequest> entity) {
        return null;
    }

    // Retorna todas as entidades
    @GetMapping()
    public ResponseEntity<List<AutonomousMaintenanceDtoResponse>> getAll() {
        return null;
    }

    // Retorna por id
    @GetMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> getById(@PathVariable Long id) {
        return null;
    }

    // Atualiza a manutenção, pega o id da manutenção autonoma e um request novo
    // para atualizar
    @PutMapping("/{id}")
    public ResponseEntity<AutonomousMaintenanceDtoResponse> update(@PathVariable Long id,
            @RequestBody AutonomousMaintenanceDtoRequest entity) {

        return null;
    }

    //Deleta por Id 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }


    //Requisições personalizadas para o front 
    @GetMapping("/situacao/{situacao}")
    public String getMethodName(@PathVariable String situacao) {
        return null;
    }
    
    

}

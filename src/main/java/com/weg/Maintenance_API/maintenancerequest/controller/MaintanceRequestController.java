package com.weg.Maintenance_API.maintenancerequest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.service.MaintenanceRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/solicitao-manutencao")
@RequiredArgsConstructor
@RestController
public class MaintanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    @PostMapping()
    public ResponseEntity<MaintenanceRequestResponse> create(
            @Valid @RequestBody MaintenanceRequestRequest maintenanceRequestRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(maintenanceRequestService.save(maintenanceRequestRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<MaintenanceRequestResponse>> getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(maintenanceRequestService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(maintenanceRequestService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> update(@PathVariable Long id,
            @Valid @RequestBody MaintenanceRequestRequest entity) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(maintenanceRequestService.update(id, entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            maintenanceRequestService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Requisição personalizada
    // setor
    @GetMapping("/setor/{setor}")
    public ResponseEntity<List<MaintenanceRequestResponse>> getBySetor(@PathVariable String setor) {
        return null;
    }

    // Prioridade
    @GetMapping("/prioridade/{prioridade}")
    public ResponseEntity<List<MaintenanceRequestResponse>> getByPrioridade(@PathVariable String prioridade) {
        return null;
    }

}

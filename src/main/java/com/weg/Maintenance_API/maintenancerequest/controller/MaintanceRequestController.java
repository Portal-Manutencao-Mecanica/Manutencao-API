package com.weg.Maintenance_API.maintenancerequest.controller;

import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceApprovalRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestPatchRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.request.MaintenanceRequestRequest;
import com.weg.Maintenance_API.maintenancerequest.dto.response.MaintenanceRequestResponse;
import com.weg.Maintenance_API.maintenancerequest.service.MaintenanceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/solicitao-manutencao")
public class MaintanceRequestController {

    private final MaintenanceRequestService service;

    @PostMapping
    public ResponseEntity<MaintenanceRequestResponse> create(
            @Valid @RequestBody MaintenanceRequestRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceRequestResponse>> getAll(Authentication authentication) {
        return ResponseEntity.ok(service.getAll(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> getById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.getById(id, authentication.getName()));
    }

    @Operation(summary = "Aprova ou reprova uma solicitação", description = "Somente o professor informado em notifiedTeacherId pode decidir uma solicitação pendente.")
    @ApiResponse(responseCode = "200", description = "Decisão registrada")
    @ApiResponse(responseCode = "403", description = "Professor não autorizado")
    @ApiResponse(responseCode = "404", description = "Solicitação inexistente")
    @PatchMapping("/{id}/aprovacao")
    public ResponseEntity<MaintenanceRequestResponse> approve(
            @PathVariable UUID id,
            @Valid @RequestBody MaintenanceApprovalRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(service.approve(
                id,
                request,
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        ));
    }

    @Operation(summary = "Aprova ou reprova uma ordem de manutenção", description = "Somente coordenadores podem decidir uma ordem pendente.")
    @PatchMapping("/{id}/ordem/aprovacao")
    public ResponseEntity<MaintenanceRequestResponse> approveWorkOrder(
            @PathVariable UUID id,
            @Valid @RequestBody MaintenanceApprovalRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(service.approveWorkOrder(
                id,
                request,
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody MaintenanceRequestRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.update(id, request, authentication.getName()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> patch(
            @PathVariable UUID id,
            @RequestBody MaintenanceRequestPatchRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.patch(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        service.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}

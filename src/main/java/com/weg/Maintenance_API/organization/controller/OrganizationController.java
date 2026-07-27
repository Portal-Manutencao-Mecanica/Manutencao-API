package com.weg.Maintenance_API.organization.controller;

import com.weg.Maintenance_API.organization.dto.CreateOrganizationRequest;
import com.weg.Maintenance_API.organization.dto.OrganizationResponse;
import com.weg.Maintenance_API.organization.dto.UpdateOrganizationRequest;
import com.weg.Maintenance_API.organization.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public OrganizationResponse create(@Valid @RequestBody CreateOrganizationRequest request) {
        return organizationService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public Page<OrganizationResponse> findAll(Pageable pageable) {
        return organizationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public OrganizationResponse findById(@PathVariable UUID id) {
        return organizationService.findById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrganizationResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        return organizationService.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public OrganizationResponse activate(@PathVariable UUID id) {
        return organizationService.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public OrganizationResponse deactivate(@PathVariable UUID id) {
        return organizationService.setActive(id, false);
    }
}

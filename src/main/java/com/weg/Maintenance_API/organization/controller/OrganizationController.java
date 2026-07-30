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

    // Cria e persiste os dados da operacao.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse create(@Valid @RequestBody CreateOrganizationRequest request) {
        return organizationService.create(request);
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public Page<OrganizationResponse> findAll(Pageable pageable) {
        return organizationService.findAll(pageable);
    }

    // Busca os dados necessarios para esta operacao.
    @GetMapping("/{id}")
    public OrganizationResponse findById(@PathVariable UUID id) {
        return organizationService.findById(id);
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}")
    public OrganizationResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        return organizationService.update(id, request);
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}/activate")
    public OrganizationResponse activate(@PathVariable UUID id) {
        return organizationService.setActive(id, true);
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/{id}/deactivate")
    public OrganizationResponse deactivate(@PathVariable UUID id) {
        return organizationService.setActive(id, false);
    }
}

package com.weg.Maintenance_API.buy.controller;



import java.util.UUID;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.request.BuyPatchRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;
import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.buy.service.BuyService;
import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compras")
public class BuyController {

    private final BuyService service;

    // Cria e persiste os dados da operacao.
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<BuyDtoResponse> create(@Valid @RequestBody BuyDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    // Busca os dados necessarios para esta operacao.
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<BuyDtoResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BuyStatus status,
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(search, status, pageable));
    }

    // Busca os dados necessarios para esta operacao.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> getById(
            @PathVariable
UUID id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody BuyDtoRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // Atualiza o estado conforme os dados informados.
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> patch(
            @PathVariable UUID id,
            @Valid @RequestBody BuyPatchRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    // Remove ou invalida os dados solicitados.
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Busca os dados necessarios para esta operacao.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/status/{status}")
    public ResponseEntity<org.springframework.data.domain.Page<BuyDtoResponse>> getByStatus(
            @PathVariable
            @ValidEnum(message = "A situacao informada e invalida.", enumClass = BuyStatus.class) String status,
            org.springframework.data.domain.Pageable pageable
    ) {
        return ResponseEntity.ok(service.getByStatus(status, pageable));
    }
}

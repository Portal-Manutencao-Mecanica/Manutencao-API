package com.weg.Maintenance_API.event.controller;

import com.weg.Maintenance_API.event.dto.response.EventResponseDto;


import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.event.dto.request.CalendarCreateRequestDto;
import com.weg.Maintenance_API.event.dto.request.CalendarUpdateRequestDto;
import com.weg.Maintenance_API.event.dto.response.CalendarResponseDto;
import com.weg.Maintenance_API.event.entity.Event;
import com.weg.Maintenance_API.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/eventos")
public class EventController {

    private final EventService eventService;

    // Cria e persiste os dados da operacao.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CalendarResponseDto> create(
            @Valid @RequestBody CalendarCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    // Busca os dados necessarios para esta operacao.
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<CalendarResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(eventService.getAll(pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/calendario")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // Busca os dados necessarios para esta operacao.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<CalendarResponseDto> getById(
            @PathVariable
UUID id
    ) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    // Atualiza o estado conforme os dados informados.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CalendarResponseDto> patch(
            @PathVariable UUID id,
            @Valid @RequestBody CalendarUpdateRequestDto request
    ) {
        return ResponseEntity.ok(eventService.patch(id, request));
    }

    // Atualiza parcialmente um evento para manter compatibilidade com clientes que usam PUT.
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CalendarResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CalendarUpdateRequestDto request
    ) {
        return ResponseEntity.ok(eventService.patch(id, request));
    }

    // Remove ou invalida os dados solicitados.
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

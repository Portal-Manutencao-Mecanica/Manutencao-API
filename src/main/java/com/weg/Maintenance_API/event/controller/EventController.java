package com.weg.Maintenance_API.event.controller;


import java.util.UUID;

import com.weg.Maintenance_API.event.dto.request.CalendarCreateRequestDto;
import com.weg.Maintenance_API.event.dto.request.CalendarUpdateRequestDto;
import com.weg.Maintenance_API.event.dto.response.CalendarResponseDto;
import com.weg.Maintenance_API.event.entity.Event;
import com.weg.Maintenance_API.event.service.EventService;
import com.weg.Maintenance_API.validation.EntityExists;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eventos")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CalendarResponseDto> create(
            @Valid @RequestBody CalendarCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CalendarResponseDto>> getAll() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarResponseDto> getById(
            @PathVariable
            @EntityExists(message = "event not found", entityClass = Event.class) UUID id
    ) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CalendarUpdateRequestDto request
    ) {
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
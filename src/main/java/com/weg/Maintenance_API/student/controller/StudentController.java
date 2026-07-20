package com.weg.Maintenance_API.student.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.buy.dto.response.StudentDtoResponse;
import com.weg.Maintenance_API.place.dto.request.PlaceRequest;
import com.weg.Maintenance_API.place.dto.response.PlaceResponse;
import com.weg.Maintenance_API.student.dto.request.StudentDtoRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/alunos")
@RestController
@RequiredArgsConstructor
public class StudentController {

    @PostMapping()
    public ResponseEntity<StudentDtoResponse> create(@Valid @RequestBody StudentDtoRequest entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<StudentDtoResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDtoResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDtoResponse> update(@PathVariable Long id,
            @Valid @RequestBody StudentDtoRequest entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

}

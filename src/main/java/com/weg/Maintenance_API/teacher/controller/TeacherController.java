package com.weg.Maintenance_API.teacher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.buy.dto.response.TeacherDtoResponse;
import com.weg.Maintenance_API.teacher.dto.request.TeacherRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
public class TeacherController {

    @PostMapping()
    public ResponseEntity<TeacherDtoResponse> create( @Valid @RequestBody TeacherRequestDto entity) {
        return null;
    }

    @GetMapping()
    public ResponseEntity<TeacherDtoResponse> getAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDtoResponse> getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDtoResponse> update(@PathVariable Long id,
            @Valid @RequestBody TeacherRequestDto entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }
}

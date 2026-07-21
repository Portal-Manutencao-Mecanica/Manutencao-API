package com.weg.Maintenance_API.classgroup.controller;

import com.weg.Maintenance_API.classgroup.service.ClassGroupService;
import com.weg.Maintenance_API.validation.EntityExists;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;
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

@RequestMapping("/turma")
@RestController
@RequiredArgsConstructor
public class ClassGroupController {
    private final ClassGroupService service;


    // Cria uma classe, o coordenador vai fazer isso e colocar alunos na turma
    @PostMapping()
    public ResponseEntity<ClassResponseDto> create(@Valid @RequestBody ClassRequestDto entity) {
        return new ResponseEntity<>(service.create(entity), HttpStatus.CREATED);
    }

    // Retorna todos as turmas
    @GetMapping()
    public ResponseEntity<List<ClassResponseDto>> getAll() {
        return new ResponseEntity<>(service.getAll(),HttpStatus.OK);
    }

    // Retorna por id
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDto> getById(@PathVariable Long id) {
        return new ResponseEntity<>(service.getById(id),HttpStatus.OK);
    }

    //Atualiza a turma por id
    @PutMapping("/{id}")
    public ResponseEntity<ClassResponseDto> update(@PathVariable Long id, @Valid @RequestBody ClassRequestDto request) {
        return new ResponseEntity<>(service.update(id,request),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @EntityExists(message = "entity is null",entityClass = ClassRequestDto.class) Long id){
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

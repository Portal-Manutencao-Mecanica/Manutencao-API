package com.weg.Maintenance_API.classgroup.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.weg.Maintenance_API.classgroup.dto.request.ClassRequestDto;
import com.weg.Maintenance_API.classgroup.dto.response.ClassResponseDto;

import io.micrometer.core.ipc.http.HttpSender.Response;
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

    // Cria uma classe, o coordenador vai fazer isso e colocar alunos na turma
    @PostMapping()
    public ResponseEntity<ClassResponseDto> create(@RequestBody ClassRequestDto entity) {
        return null;
    }

    // Retorna todos as turmas
    @GetMapping()
    public ResponseEntity<List<ClassResponseDto>> getAll() {
        return null;
    }

    // Retorna por id
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDto> getById(@PathVariable Long id) {
        return null;
    }

    //Atualiza a turma por id
    @PutMapping("/{id}")
    public ResponseEntity<ClassResponseDto> update(@PathVariable Long id, @RequestBody ClassRequestDto entity) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        return null;
    }

}

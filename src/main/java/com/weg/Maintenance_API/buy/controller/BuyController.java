package com.weg.Maintenance_API.buy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.response.BuyDtoResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/compras")
@AllArgsConstructor
@RestController
public class BuyController {

    // Cria uma compra
    @PostMapping()
    public ResponseEntity<BuyDtoResponse> create(@Valid @RequestBody BuyDtoRequest entity) {
        return null;
    }

    // Retorna todas as compras, sera usada na hora de ver historico, ou quando o
    // coordenador ver as compra para aprovar
    @GetMapping()
    public ResponseEntity<List<BuyDtoResponse>> getAll() {
        return null;
    }

    // Retorna por Id
    @GetMapping("/{id}")
    public ResponseEntity<List<BuyDtoResponse>> getById(@PathVariable Long id) {
        return null;
    }

    // Delete por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return null;
    }

    // Requisições personalizadas
    // Essa Requisição retorna por status da compra
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BuyDtoResponse>> getByStatus(@Valid @PathVariable String status) {
        return null;
    }

}

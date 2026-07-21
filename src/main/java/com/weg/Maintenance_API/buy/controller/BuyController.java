package com.weg.Maintenance_API.buy.controller;

import java.util.List;

import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.buy.service.BuyService;
import com.weg.Maintenance_API.enums.Status;
import com.weg.Maintenance_API.validation.EntityExists;
import com.weg.Maintenance_API.validation.enumValidator.ValidEnum;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("/compras")
@AllArgsConstructor
@RestController
public class BuyController {
    private final BuyService service;

    // Cria uma compra
    @PostMapping()
    public ResponseEntity<BuyDtoResponse> create(@Valid @RequestBody BuyDtoRequest request) {
        return null;
    }

    // Retorna todas as compras, sera usada na hora de ver historico, ou quando o
    // coordenador ver as compra para aprovar
    @GetMapping()
    public ResponseEntity<List<BuyDtoResponse>> getAll() {
        return new ResponseEntity<>(service.getAll(),HttpStatus.OK);
    }

    // Retorna por Id
    @GetMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> getById(@PathVariable @EntityExists(message = "entity not founded",entityClass = Buy.class) Long id) {
        return new ResponseEntity<>(service.getById(id),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyDtoResponse> update(@PathVariable Long id, @Valid @RequestBody BuyDtoRequest entity) {
        return null;
    }

    // Delete por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @EntityExists(message = "entity not found",entityClass = Buy.class) Long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Requisições personalizadas
    // Essa Requisição retorna por status da compra
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BuyDtoResponse>> getByStatus(@PathVariable @ValidEnum(message = "enum is invalid", enumClass = Status.class) String status) {
        return new ResponseEntity<>(service.getByStatus(status),HttpStatus.OK);
    }

}

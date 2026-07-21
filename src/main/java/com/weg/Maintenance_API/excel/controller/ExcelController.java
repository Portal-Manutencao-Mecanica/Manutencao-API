package com.weg.Maintenance_API.excel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/excel")
@RestController
@AllArgsConstructor
public class ExcelController {

    private final UserImportService userImportService;

    @PostMapping("/import")
    public ResponseEntity<String> importar(@RequestParam("file") MultipartFile file) {

        userImportService.importUsers(file);

        return ResponseEntity.ok("Usuários importados com sucesso.");
    }
}

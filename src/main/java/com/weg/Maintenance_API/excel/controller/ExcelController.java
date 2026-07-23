package com.weg.Maintenance_API.excel.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.weg.Maintenance_API.excel.service.UserImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/excel")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ExcelController {

    private final UserImportService userImportService;

    @PostMapping("/import")
    @PreAuthorize("denyAll()")
    public ResponseEntity<String> importar(@RequestParam("file") MultipartFile file) {
        userImportService.importUsers(file);
        return ResponseEntity.ok("Usuários importados com sucesso.");
    }
}

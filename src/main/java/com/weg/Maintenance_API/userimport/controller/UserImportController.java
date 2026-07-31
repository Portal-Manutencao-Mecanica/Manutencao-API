package com.weg.Maintenance_API.userimport.controller;

import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.userimport.dto.UserImportResponse;
import com.weg.Maintenance_API.userimport.service.UserImportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserImportController {

    private final UserImportService userImportService;

    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Importa usuários por CSV ou XLSX",
            description = "O arquivo deve conter name, username, email, role, organization e classGroupIds. "
                    + "organization aceita somente SENAI, WEG ou OTHER. ADMIN pode importar todas as roles; "
                    + "COORDENADOR somente ALUNO e PROFESSOR da própria organização."
    )
    // Executa a operacao deste metodo.
    public UserImportResponse importUsers(
            @RequestPart("file") MultipartFile file,
            Authentication authentication,
            HttpServletRequest request
    ) {
        return userImportService.importUsers(
                file,
                authentication.getName(),
                ClientRequestMetadata.from(request)
        );
    }
}

package com.weg.Maintenance_API.user.controller;

import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.dto.response.UserCreationResponse;
import com.weg.Maintenance_API.user.service.UserCreationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserCreationService userCreationService;

    // Cria usuario e perfil especifico no endpoint manual unico.
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cria usuario e perfil especifico",
            description = "O username e gerado automaticamente pelo nome completo e uma sequencia numerica. Envie somente o bloco correspondente a role: studentData para ALUNO, teacherData para PROFESSOR e nenhum bloco especifico para COORDENADOR ou ADMIN. ADMIN cria qualquer role. COORDENADOR cria apenas ALUNO ou PROFESSOR.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserRequest.class),
                            examples = {
                                    @ExampleObject(name = "ALUNO", value = """
                                            {"name":"Joao da Silva","email":"joao@organizacao.com","numberCard":"1001","role":"ALUNO","organizationId":"00000000-0000-4000-8000-000000000001","studentData":{"classGroupIds":["00000000-0000-4000-8000-000000000010"]}}
                                            """),
                                    @ExampleObject(name = "PROFESSOR", value = """
                                            {"name":"Maria Souza","email":"maria@organizacao.com","numberCard":"2001","role":"PROFESSOR","organizationId":"00000000-0000-4000-8000-000000000001","teacherData":{"classGroupIds":["00000000-0000-4000-8000-000000000010"]}}
                                            """),
                                    @ExampleObject(name = "COORDENADOR", value = """
                                            {"name":"Ana Lima","email":"ana@organizacao.com","numberCard":"3001","role":"COORDENADOR","organizationId":"00000000-0000-4000-8000-000000000001"}
                                            """),
                                    @ExampleObject(name = "ADMIN", value = """
                                            {"name":"Administrador","email":"admin@organizacao.com","numberCard":"4001","role":"ADMIN","organizationId":"00000000-0000-4000-8000-000000000001"}
                                            """)
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario e perfil criados."),
            @ApiResponse(responseCode = "400", description = "Dados do perfil nao correspondem a role."),
            @ApiResponse(responseCode = "403", description = "Ator sem permissao para a role ou organizacao."),
            @ApiResponse(responseCode = "404", description = "Organizacao ou turma nao encontrada."),
            @ApiResponse(responseCode = "409", description = "Username ou e-mail ja utilizado.")
    })
    public UserCreationResponse create(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest
    ) {
        return userCreationService.create(
                request,
                ClientRequestMetadata.from(httpRequest)
        );
    }
}

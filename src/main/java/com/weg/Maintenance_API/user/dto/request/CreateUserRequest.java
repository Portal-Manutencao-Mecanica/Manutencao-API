package com.weg.Maintenance_API.user.dto.request;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.user.validation.ValidUserProfileData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@ValidUserProfileData
@Schema(description = "Dados para criacao manual de um usuario e de seu perfil especifico.")
public record CreateUserRequest(
        @NotBlank(message = "O nome e obrigatorio.")
        @Size(max = 150, message = "O nome deve possuir no maximo 150 caracteres.")
        String name,

        @NotBlank(message = "O username e obrigatorio.")
        @Size(min = 3, max = 50, message = "O username deve possuir entre 3 e 50 caracteres.")
        String username,

        @NotBlank(message = "O e-mail e obrigatorio.")
        @Email(message = "O e-mail informado e invalido.")
        @Size(max = 150, message = "O e-mail deve possuir no maximo 150 caracteres.")
        String email,

        @NotNull(message = "A role e obrigatoria.")
        Role role,

        @Schema(description = "UUID da organizacao. Para coordenador autenticado, a propria organizacao e usada.")
        UUID organizationId,

        @Valid
        @Schema(description = "Obrigatorio somente para a role ALUNO.")
        StudentDataRequest studentData,

        @Valid
        @Schema(description = "Obrigatorio somente para a role PROFESSOR.")
        TeacherDataRequest teacherData
) {
}

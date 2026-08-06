package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.user.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class UserManagementPermissionService {

    // Valida a regra aplicada por este metodo.
    public void validateCanCreate(
            User actor,
            Role targetRole,
            Organization targetOrganization
    ) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }

        boolean allowedRole = targetRole == Role.PROFESSOR || targetRole == Role.ALUNO;
        if (actor.getRole() == Role.COORDENADOR && allowedRole) {
            return;
        }

        if (actor.getRole() == Role.COORDENADOR && !allowedRole) {
            throw new AccessDeniedException(
                    "Coordenadores sÃ³ podem criar usuÃ¡rios com as roles PROFESSOR ou ALUNO."
            );
        }
        throw new AccessDeniedException("VocÃª nÃ£o possui permissÃ£o para criar usuÃ¡rios.");
    }

    // Valida a regra aplicada por este metodo.
    public void validateCanImport(
            User actor,
            Role targetRole,
            Organization targetOrganization
    ) {
        validateCanCreate(actor, targetRole, targetOrganization);
    }

    // Valida a regra aplicada por este metodo.
    public void validateCanManage(User actor, User target) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        boolean allowedTargetRole =
                target.getRole() == Role.PROFESSOR || target.getRole() == Role.ALUNO;
        if (actor.getRole() == Role.COORDENADOR && allowedTargetRole) {
            return;
        }
        throw new AccessDeniedException(
                "Coordenadores só podem inativar professores e alunos."
        );
    }

    // Valida a regra aplicada por este metodo.
    public void validateCanResendCredentials(User actor, User target) {
        if (actor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Somente administradores podem reenviar credenciais.");
        }
    }

    // Valida a regra aplicada por este metodo.
    public void validateCanChangeRole(User actor) {
        if (actor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Somente administradores podem alterar roles.");
        }
    }

}

package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.user.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        boolean sameOrganization = Objects.equals(
                actor.getOrganization().getId(),
                targetOrganization.getId()
        );
        if (actor.getRole() == Role.COORDENADOR && allowedRole && sameOrganization) {
            return;
        }

        if (actor.getRole() == Role.COORDENADOR && !allowedRole) {
            throw new AccessDeniedException(
                    "Coordenadores sÃ³ podem criar usuÃ¡rios com as roles PROFESSOR ou ALUNO."
            );
        }
        if (actor.getRole() == Role.COORDENADOR) {
            throw new AccessDeniedException(
                    "Coordenadores sÃ³ podem criar usuÃ¡rios em sua prÃ³pria organizaÃ§Ã£o."
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
        if (targetRole != Role.PROFESSOR && targetRole != Role.ALUNO) {
            throw new AccessDeniedException(
                    "A importaÃ§Ã£o permite apenas usuÃ¡rios com as roles PROFESSOR ou ALUNO."
            );
        }
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() != Role.COORDENADOR) {
            throw new AccessDeniedException("VocÃª nÃ£o possui permissÃ£o para importar usuÃ¡rios.");
        }
        if (!sameOrganization(actor.getOrganization(), targetOrganization)) {
            throw new AccessDeniedException(
                    "Coordenadores sÃ³ podem importar usuÃ¡rios para sua prÃ³pria organizaÃ§Ã£o."
            );
        }
    }

    // Valida a regra aplicada por este metodo.
    public void validateCanManage(User actor, User target) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        boolean allowedTargetRole =
                target.getRole() == Role.PROFESSOR || target.getRole() == Role.ALUNO;
        if (actor.getRole() == Role.COORDENADOR
                && allowedTargetRole
                && sameOrganization(actor.getOrganization(), target.getOrganization())) {
            return;
        }
        throw new AccessDeniedException(
                "Coordenadores sÃ³ podem administrar professores e alunos da prÃ³pria organizaÃ§Ã£o."
        );
    }

    // Valida a regra aplicada por este metodo.
    public void validateCanResendCredentials(User actor, User target) {
        validateCanManage(actor, target);
    }

    // Valida a regra aplicada por este metodo.
    public void validateCanChangeRole(User actor) {
        if (actor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Somente administradores podem alterar roles.");
        }
    }

    // Executa a operacao deste metodo.
    private boolean sameOrganization(
            Organization first,
            Organization second
    ) {
        return first != null
                && second != null
                && Objects.equals(first.getId(), second.getId());
    }
}

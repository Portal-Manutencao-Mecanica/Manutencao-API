package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.user.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserManagementPermissionService {

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
                    "Coordenadores só podem criar usuários com as roles PROFESSOR ou ALUNO."
            );
        }
        if (actor.getRole() == Role.COORDENADOR) {
            throw new AccessDeniedException(
                    "Coordenadores só podem criar usuários em sua própria organização."
            );
        }
        throw new AccessDeniedException("Você não possui permissão para criar usuários.");
    }

    public void validateCanImport(
            User actor,
            Role targetRole,
            Organization targetOrganization
    ) {
        if (targetRole != Role.PROFESSOR && targetRole != Role.ALUNO) {
            throw new AccessDeniedException(
                    "A importação permite apenas usuários com as roles PROFESSOR ou ALUNO."
            );
        }
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() != Role.COORDENADOR) {
            throw new AccessDeniedException("Você não possui permissão para importar usuários.");
        }
        if (!sameOrganization(actor.getOrganization(), targetOrganization)) {
            throw new AccessDeniedException(
                    "Coordenadores só podem importar usuários para sua própria organização."
            );
        }
    }

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
                "Coordenadores só podem administrar professores e alunos da própria organização."
        );
    }

    public void validateCanResendCredentials(User actor, User target) {
        validateCanManage(actor, target);
    }

    public void validateCanChangeRole(User actor) {
        if (actor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Somente administradores podem alterar roles.");
        }
    }

    private boolean sameOrganization(
            Organization first,
            Organization second
    ) {
        return first != null
                && second != null
                && Objects.equals(first.getId(), second.getId());
    }
}

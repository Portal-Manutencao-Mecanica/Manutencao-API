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
}

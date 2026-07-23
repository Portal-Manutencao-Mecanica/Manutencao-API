package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.entity.OrganizationType;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserManagementPermissionServiceTest {

    private final UserManagementPermissionService permissionService =
            new UserManagementPermissionService();

    @Test
    void adminCanCreateEveryRole() {
        Organization organization = organization();
        Admin admin = new Admin("Admin", "admin@local.com", "hash");
        admin.setOrganization(organization);

        for (Role role : Role.values()) {
            assertDoesNotThrow(() ->
                    permissionService.validateCanCreate(admin, role, organization));
        }
    }

    @Test
    void coordinatorCanOnlyCreateTeacherOrStudentInOwnOrganization() {
        Organization ownOrganization = organization();
        Organization otherOrganization = organization();
        otherOrganization.setId(UUID.randomUUID());
        Coordinator coordinator =
                new Coordinator("Coord", "coord@local.com", "hash");
        coordinator.setOrganization(ownOrganization);

        assertDoesNotThrow(() -> permissionService.validateCanCreate(
                coordinator,
                Role.PROFESSOR,
                ownOrganization
        ));
        assertDoesNotThrow(() -> permissionService.validateCanCreate(
                coordinator,
                Role.ALUNO,
                ownOrganization
        ));
        assertThrows(AccessDeniedException.class, () ->
                permissionService.validateCanCreate(
                        coordinator,
                        Role.ADMIN,
                        ownOrganization
                ));
        assertThrows(AccessDeniedException.class, () ->
                permissionService.validateCanCreate(
                        coordinator,
                        Role.PROFESSOR,
                        otherOrganization
                ));
    }

    private Organization organization() {
        Organization organization =
                new Organization("Local", OrganizationType.OTHER, "local.com");
        organization.setId(UUID.randomUUID());
        return organization;
    }
}

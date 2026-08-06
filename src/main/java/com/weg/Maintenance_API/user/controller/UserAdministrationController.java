package com.weg.Maintenance_API.user.controller;

import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.user.dto.request.ChangeUserRoleRequest;
import com.weg.Maintenance_API.user.dto.request.UserStatusChangeRequest;
import com.weg.Maintenance_API.user.dto.request.UpdateUserRequest;
import com.weg.Maintenance_API.user.dto.response.CredentialResendResponse;
import com.weg.Maintenance_API.user.dto.response.ManagedUserResponse;
import com.weg.Maintenance_API.user.service.UserAdministrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAdministrationController {

    private final UserAdministrationService userAdministrationService;

    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @GetMapping
    public Page<ManagedUserResponse> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean enabled,
            Pageable pageable,
            Authentication authentication
    ) {
        return userAdministrationService.getAll(
                search,
                role,
                enabled,
                pageable,
                authentication.getName()
        );
    }

    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ManagedUserResponse getById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return userAdministrationService.getById(id, authentication.getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ManagedUserResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userAdministrationService.update(
                id,
                request,
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Executa a operacao deste metodo.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/block")
    public ManagedUserResponse block(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userAdministrationService.block(
                id,
                request.reason(),
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Executa a operacao deste metodo.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/unblock")
    public ManagedUserResponse unblock(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userAdministrationService.unblock(
                id,
                request.reason(),
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Atualiza o estado conforme os dados informados.
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ManagedUserResponse deactivate(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userAdministrationService.deactivate(
                id,
                request.reason(),
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Executa a operacao deste metodo.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reactivate")
    public ManagedUserResponse reactivate(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusChangeRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userAdministrationService.reactivate(
                id,
                request.reason(),
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Atualiza o estado conforme os dados informados.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ManagedUserResponse changeRole(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeUserRoleRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userAdministrationService.changeRole(
                id,
                request.role(),
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Executa a operacao deste metodo.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/resend-credentials")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CredentialResendResponse resendCredentials(
            @PathVariable UUID id,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userAdministrationService.resendCredentials(
                id,
                authentication.getName(),
                ClientRequestMetadata.from(httpRequest)
        );
    }
}

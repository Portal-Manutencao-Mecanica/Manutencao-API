package com.weg.Maintenance_API.user.controller;

import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.user.dto.request.ChangeOwnPasswordRequest;
import com.weg.Maintenance_API.user.dto.request.UpdateNotificationPreferencesRequest;
import com.weg.Maintenance_API.user.dto.request.UpdateOwnProfileRequest;
import com.weg.Maintenance_API.user.dto.response.UserProfileResponse;
import com.weg.Maintenance_API.user.service.UserPasswordService;
import com.weg.Maintenance_API.user.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserPasswordService userPasswordService;

    // Busca os dados necessarios para esta operacao.
    @GetMapping
    public UserProfileResponse getProfile(Authentication authentication) {
        return userProfileService.get(authentication.getName());
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping
    public UserProfileResponse updateProfile(
            @Valid @RequestBody UpdateOwnProfileRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userProfileService.update(
                authentication.getName(),
                request,
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @Valid @RequestBody ChangeOwnPasswordRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        userPasswordService.changeOwnPassword(
                authentication.getName(),
                request,
                ClientRequestMetadata.from(httpRequest)
        );
    }

    // Atualiza o estado conforme os dados informados.
    @PatchMapping("/preferences")
    public UserProfileResponse updatePreferences(
            @Valid @RequestBody UpdateNotificationPreferencesRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return userProfileService.updatePreferences(
                authentication.getName(),
                request,
                ClientRequestMetadata.from(httpRequest)
        );
    }
}

package com.weg.Maintenance_API.user.controller;

import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.user.dto.request.ChangeOwnPasswordRequest;
import com.weg.Maintenance_API.user.dto.request.UpdateNotificationPreferencesRequest;
import com.weg.Maintenance_API.user.dto.request.UpdateOwnProfileRequest;
import com.weg.Maintenance_API.user.dto.response.ProfilePhotoResponse;
import com.weg.Maintenance_API.user.dto.response.UserProfileResponse;
import com.weg.Maintenance_API.user.service.ProfilePhotoService;
import com.weg.Maintenance_API.user.service.UserPasswordService;
import com.weg.Maintenance_API.user.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users/me")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserPasswordService userPasswordService;
    private final ProfilePhotoService profilePhotoService;

    @GetMapping
    public UserProfileResponse getProfile(Authentication authentication) {
        return userProfileService.get(authentication.getName());
    }

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

    @PostMapping(
            value = "/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProfilePhotoResponse uploadPhoto(
            @RequestPart("file") MultipartFile file,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        return profilePhotoService.upload(
                authentication.getName(),
                file,
                ClientRequestMetadata.from(httpRequest)
        );
    }

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

package com.weg.Maintenance_API.auth.password.controller;

import com.weg.Maintenance_API.auth.password.dto.ForgotPasswordRequest;
import com.weg.Maintenance_API.auth.password.dto.MessageResponse;
import com.weg.Maintenance_API.auth.password.dto.ResetPasswordRequest;
import com.weg.Maintenance_API.auth.password.dto.ValidatePasswordResetTokenResponse;
import com.weg.Maintenance_API.auth.password.service.PasswordResetTokenService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
@Validated
public class PasswordRecoveryController {

    private static final String GENERIC_MESSAGE =
            "Caso o e-mail esteja cadastrado, as instruções serão enviadas.";

    private final PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/forgot")
    public MessageResponse forgot(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        passwordResetTokenService.requestReset(
                request.email(),
                ClientRequestMetadata.from(httpRequest)
        );
        return new MessageResponse(GENERIC_MESSAGE);
    }

    @GetMapping("/validate")
    public ValidatePasswordResetTokenResponse validate(
            @RequestParam @NotBlank String token
    ) {
        return new ValidatePasswordResetTokenResponse(
                passwordResetTokenService.validate(token)
        );
    }

    @PostMapping("/reset")
    public MessageResponse reset(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        passwordResetTokenService.reset(
                request,
                ClientRequestMetadata.from(httpRequest)
        );
        return new MessageResponse("Senha redefinida com sucesso.");
    }
}

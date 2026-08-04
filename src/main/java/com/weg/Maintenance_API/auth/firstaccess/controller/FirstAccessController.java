package com.weg.Maintenance_API.auth.firstaccess.controller;

import com.weg.Maintenance_API.auth.firstaccess.dto.CompleteFirstAccessRequest;
import com.weg.Maintenance_API.auth.firstaccess.service.FirstAccessService;
import com.weg.Maintenance_API.auth.password.dto.MessageResponse;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me/first-access")
@RequiredArgsConstructor
public class FirstAccessController {

    private final FirstAccessService firstAccessService;

    @PostMapping("/code")
    public MessageResponse requestCode(
            Authentication authentication,
            HttpServletRequest request
    ) {
        firstAccessService.requestCode(
                authentication.getName(),
                ClientRequestMetadata.from(request)
        );
        return new MessageResponse("Código enviado para o e-mail da conta.");
    }

    @PostMapping("/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(
            @Valid @RequestBody CompleteFirstAccessRequest body,
            Authentication authentication,
            HttpServletRequest request
    ) {
        firstAccessService.complete(
                authentication.getName(),
                body,
                ClientRequestMetadata.from(request)
        );
    }
}

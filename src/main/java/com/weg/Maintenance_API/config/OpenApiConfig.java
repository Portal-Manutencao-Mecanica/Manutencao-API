package com.weg.Maintenance_API.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Portal de ManutenÃƒÂ§ÃƒÂ£o API",
                version = "1.0",
                description = "API REST para os fluxos do Portal de ManutenÃƒÂ§ÃƒÂ£o."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Informe o access token JWT."
)
public class OpenApiConfig {
    // This class configures Swagger documentation only; it does not secure the API.
}

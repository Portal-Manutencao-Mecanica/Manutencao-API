package com.weg.Maintenance_API.config.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StringUtils;

public final class RuntimeConfiguredJwtDecoder implements JwtDecoder {

    private final String jwkSetUri;
    private volatile JwtDecoder delegate;

    public RuntimeConfiguredJwtDecoder(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        return getDelegate().decode(token);
    }

    private JwtDecoder getDelegate() {
        if (!StringUtils.hasText(jwkSetUri)) {
            throw new JwtException("JWT validation is not configured");
        }

        JwtDecoder current = delegate;
        if (current == null) {
            synchronized (this) {
                current = delegate;
                if (current == null) {
                    current = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
                    delegate = current;
                }
            }
        }
        return current;
    }
}

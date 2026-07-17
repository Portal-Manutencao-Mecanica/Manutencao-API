package com.weg.Maintenance_API.config.security;

import com.weg.Maintenance_API.enums.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class JwtRoleAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        String email = jwt.getClaimAsString("email");
        String principalName = StringUtils.hasText(email) ? email : jwt.getSubject();
        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();
        addClaimValues(roles, jwt.getClaim("role"));
        addClaimValues(roles, jwt.getClaim("roles"));

        return roles.stream()
                .map(this::normalizeRole)
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    private void addClaimValues(Set<String> roles, Object claim) {
        if (claim instanceof String role) {
            roles.add(role);
        } else if (claim instanceof Collection<?> collection) {
            collection.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(roles::add);
        } else if (claim instanceof String[] array) {
            roles.addAll(List.of(array));
        }
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }

        String normalized = role.trim().toUpperCase(Locale.ROOT);
        String enumName = normalized.startsWith("ROLE_")
                ? normalized.substring("ROLE_".length())
                : normalized;

        try {
            Role.valueOf(enumName);
            return "ROLE_" + enumName;
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}

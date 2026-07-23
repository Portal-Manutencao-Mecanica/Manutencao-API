package com.weg.Maintenance_API.config.security;

import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserAccessStateFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        if (user == null
                || !user.isEnabled()
                || !user.isAccountNonLocked()
                || user.isTemporarilyLocked()
                || !user.getOrganization().isActive()
                || !matchesCurrentSecurityState(authentication, user)) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException(
                            "A conta não está disponível."
                    )
            );
            return;
        }

        if (user.isPasswordChangeRequired() && !isFirstAccessAllowed(request)) {
            accessDeniedHandler.handle(
                    request,
                    response,
                    new AccessDeniedException(
                            "Altere a senha temporária antes de acessar outras funcionalidades."
                    )
            );
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean matchesCurrentSecurityState(
            Authentication authentication,
            User user
    ) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            return true;
        }
        Object versionClaim = jwtAuthentication.getToken().getClaim("securityVersion");
        String roleClaim = jwtAuthentication.getToken().getClaimAsString("role");
        String organizationClaim =
                jwtAuthentication.getToken().getClaimAsString("organizationId");
        return versionClaim instanceof Number number
                && number.longValue() == user.getSecurityVersion()
                && user.getRole().name().equals(roleClaim)
                && user.getOrganization().getId().toString().equals(organizationClaim);
    }

    private boolean isFirstAccessAllowed(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return (HttpMethod.GET.matches(request.getMethod()) && path.equals("/users/me"))
                || (HttpMethod.PATCH.matches(request.getMethod())
                    && path.equals("/users/me/password"))
                || (HttpMethod.POST.matches(request.getMethod())
                    && path.equals("/auth/logout"));
    }
}

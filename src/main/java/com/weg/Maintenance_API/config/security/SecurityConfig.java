package com.weg.Maintenance_API.config.security;

import com.weg.Maintenance_API.config.idempotency.IdempotencyFilter;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtRoleAuthenticationConverter jwtAuthenticationConverter,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler,
            UserAccessStateFilter userAccessStateFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(applicationPathEquals("/actuator/health")).permitAll()
                        .requestMatchers(applicationPathStartsWith("/actuator/")).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/password/forgot").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/password/validate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/password/reset").permitAll()
                        .requestMatchers(HttpMethod.POST, "/notification").denyAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users", "/users/import")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/*/deactivate")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH,
                                "/users/*/block",
                                "/users/*/unblock",
                                "/users/*/reactivate",
                                "/users/*/role")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/*/resend-credentials")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/*")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/maquinas", "/turma", "/maquina-log", "/equipamento",
                                "/material-apoio", "/designacao", "/lugar", "/organizations")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/maquinas/**", "/turma/**", "/maquina-log/**", "/equipamento/**",
                                "/material-apoio/**", "/designacao/**", "/lugar/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH,
                                "/maquinas/**", "/turma/**", "/maquina-log/**", "/equipamento/**",
                                "/material-apoio/**", "/designacao/**", "/lugar/**", "/organizations/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/maquinas/**", "/turma/**", "/maquina-log/**", "/equipamento/**",
                                "/material-apoio/**", "/designacao/**", "/lugar/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/eventos")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/eventos/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/eventos/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/eventos/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/manutencao-autonoma")
                        .hasAnyRole("PROFESSOR", "COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/manutencao-autonoma/*/aprovacao")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/manutencao-autonoma/*")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/manutencao-autonoma/*")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/manutencao-autonoma/**")
                        .hasAnyRole("PROFESSOR", "COORDENADOR", "ALUNO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/5s")
                        .hasAnyRole("PROFESSOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/5s/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/5s/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/5s/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/solicitao-manutencao/*/aprovacao")
                        .hasAnyRole("PROFESSOR", "COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/solicitao-manutencao/*/ordem/aprovacao")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/solicitao-manutencao/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/solicitao-manutencao/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/solicitao-manutencao/**")
                        .hasAnyRole("COORDENADOR", "ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterAfter(userAccessStateFilter, BearerTokenAuthenticationFilter.class);

        http.addFilterAfter(new IdempotencyFilter(), UserAccessStateFilter.class);

        return http.build();
    }

    private static RequestMatcher applicationPathEquals(String expectedPath) {
        return request -> applicationPath(request).equals(expectedPath);
    }

    private static RequestMatcher applicationPathStartsWith(String expectedPrefix) {
        return request -> applicationPath(request).startsWith(expectedPrefix);
    }

    private static String applicationPath(jakarta.servlet.http.HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            DatabaseUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider
    ) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecretKey jwtSecretKey(@Value("${app.jwt.secret}") String secret) {
        byte[] decodedSecret = Base64.getDecoder().decode(secret);
        if (decodedSecret.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET deve possuir ao menos 256 bits codificados em Base64.");
        }
        return new SecretKeySpec(decodedSecret, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.HS256)
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("portal-manutencao-api"));
        return decoder;
    }
}

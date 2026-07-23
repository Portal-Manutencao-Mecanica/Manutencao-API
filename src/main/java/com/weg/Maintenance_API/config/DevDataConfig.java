package com.weg.Maintenance_API.config;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevDataConfig {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Bean
    CommandLineRunner createDevelopmentAdmin(PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmailIgnoreCase("admin@local.com").isEmpty()) {
                Admin admin = new Admin(
                        "Administrador Local",
                        "admin@local.com",
                        passwordEncoder.encode("12345678")
                );
                admin.setUsername("admin.local");
                admin.setOrganization(
                        organizationRepository.findByEmailDomainIgnoreCase("local.com")
                                .orElseThrow(() -> new IllegalStateException(
                                        "A organização local não foi criada pelas migrations."
                                ))
                );
                admin.setPasswordChangeRequired(false);

                userRepository.save(admin);
            }
        };
    }
}

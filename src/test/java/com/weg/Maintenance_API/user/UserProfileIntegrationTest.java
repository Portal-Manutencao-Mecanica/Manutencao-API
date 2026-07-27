package com.weg.Maintenance_API.user;

import com.jayway.jsonpath.JsonPath;
import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.repository.AuditLogRepository;
import com.weg.Maintenance_API.auth.password.repository.PasswordResetTokenRepository;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.enums.OrganizationType;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.preference.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileIntegrationTest {

    private static final String PASSWORD = "ProfilePass@123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private NotificationPreferenceRepository preferenceRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String accessToken;

    @BeforeEach
    void createUser() throws Exception {
        Organization organization = organizationRepository.saveAndFlush(
                new Organization(
                        "Profile " + UUID.randomUUID(),
                        OrganizationType.OTHER,
                        "profile.test"
                )
        );
        Admin admin = new Admin(
                "Profile User",
                "profile@profile.test",
                passwordEncoder.encode(PASSWORD)
        );
        admin.setUsername("profile." + UUID.randomUUID().toString().substring(0, 8));
        admin.setOrganization(organization);
        userRepository.saveAndFlush(admin);

        String loginBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"profile@profile.test","password":"%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        accessToken = JsonPath.read(loginBody, "$.accessToken");
    }

    @AfterEach
    void cleanUp() {
        preferenceRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    void userUpdatesOnlyAllowedProfileFieldsAndPreferences() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Profile User"))
                .andExpect(jsonPath("$.preferences.emailEnabled").value(true));

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Novo Nome"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"));

        mockMvc.perform(patch("/users/me/preferences")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emailEnabled":false,
                                  "purchaseNotifications":false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences.emailEnabled").value(false))
                .andExpect(jsonPath("$.preferences.purchaseNotifications").value(false));

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Tentativa","role":"ADMIN"}
                                """))
                .andExpect(status().isBadRequest());
    }

    private String bearer() {
        return "Bearer " + accessToken;
    }
}

package com.weg.Maintenance_API.auth;

import com.jayway.jsonpath.JsonPath;
import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.repository.AuditLogRepository;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.entity.OrganizationType;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    private static final String EMAIL = "auth.flow@local.test";
    private static final String PASSWORD = "ValidPass@123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID userId;
    private UUID organizationId;

    @BeforeEach
    void createUser() {
        Organization organization = new Organization(
                "Auth Flow " + UUID.randomUUID(),
                OrganizationType.OTHER,
                "local.test"
        );
        organization = organizationRepository.saveAndFlush(organization);
        organizationId = organization.getId();

        Admin admin = new Admin(
                "Auth Flow Admin",
                EMAIL,
                passwordEncoder.encode(PASSWORD)
        );
        admin.setUsername("auth.flow." + UUID.randomUUID().toString().substring(0, 8));
        admin.setOrganization(organization);
        admin.setPasswordChangeRequired(false);
        admin = userRepository.saveAndFlush(admin);
        userId = admin.getId();
    }

    @AfterEach
    void cleanUp() {
        refreshTokenRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteById(userId);
        organizationRepository.deleteById(organizationId);
    }

    @Test
    void loginRefreshAuthenticatedAccessAndLogoutWorkTogether() throws Exception {
        String loginJson = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(EMAIL, PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String loginBody = loginResult.getResponse().getContentAsString();
        String firstAccessToken = JsonPath.read(loginBody, "$.accessToken");
        String firstRefreshToken = JsonPath.read(loginBody, "$.refreshToken");
        assertNotNull(firstAccessToken);
        assertNotNull(firstRefreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(firstRefreshToken)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshBody = refreshResult.getResponse().getContentAsString();
        String secondAccessToken = JsonPath.read(refreshBody, "$.accessToken");
        String secondRefreshToken = JsonPath.read(refreshBody, "$.refreshToken");
        assertNotEquals(firstRefreshToken, secondRefreshToken);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + secondAccessToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + secondAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(secondRefreshToken)))
                .andExpect(status().isNoContent());
    }
}

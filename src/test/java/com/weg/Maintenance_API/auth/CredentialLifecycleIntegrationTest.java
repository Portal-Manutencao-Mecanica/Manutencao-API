package com.weg.Maintenance_API.auth;

import com.jayway.jsonpath.JsonPath;
import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.repository.AuditLogRepository;
import com.weg.Maintenance_API.auth.password.entity.PasswordResetToken;
import com.weg.Maintenance_API.auth.password.repository.PasswordResetTokenRepository;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.auth.service.SecureTokenService;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.entity.OrganizationType;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.preference.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CredentialLifecycleIntegrationTest {

    private static final String CURRENT_PASSWORD = "Temporary@123";
    private static final String NEW_PASSWORD = "ChangedPass@456";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private NotificationPreferenceRepository preferenceRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecureTokenService secureTokenService;

    @MockitoBean
    private JavaMailSender mailSender;

    private Organization organization;

    @BeforeEach
    void setUp() {
        clearInvocations(mailSender);
        organization = organizationRepository.saveAndFlush(
                new Organization(
                        "Credential " + UUID.randomUUID(),
                        OrganizationType.OTHER,
                        "credential.test"
                )
        );
    }

    @AfterEach
    void cleanUp() {
        passwordResetTokenRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        auditLogRepository.deleteAll();
        preferenceRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    void firstAccessCanOnlyReadProfileChangePasswordAndLogout() throws Exception {
        Admin user = createUser("first.access@credential.test", true);
        String loginBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(user.getEmail(), CURRENT_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passwordChangeRequired").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String accessToken = JsonPath.read(loginBody, "$.accessToken");
        String refreshToken = JsonPath.read(loginBody, "$.refreshToken");

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/maquinas")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("ACCESS_DENIED"));

        mockMvc.perform(patch("/users/me/password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword":"%s",
                                  "newPassword":"%s",
                                  "passwordConfirmation":"%s"
                                }
                                """.formatted(
                                CURRENT_PASSWORD,
                                NEW_PASSWORD,
                                NEW_PASSWORD
                        )))
                .andExpect(status().isNoContent());

        Admin changed = (Admin) userRepository.findById(user.getId()).orElseThrow();
        assertFalse(changed.isPasswordChangeRequired());
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, changed.getPassword()));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/maquinas")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());

        String newLoginBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(user.getEmail(), NEW_PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String newAccessToken = JsonPath.read(newLoginBody, "$.accessToken");
        mockMvc.perform(get("/maquinas")
                        .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void passwordResetTokenIsHashedSingleUseAndRevokesSessions() throws Exception {
        Admin user = createUser("reset@credential.test", false);
        String loginBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(user.getEmail(), CURRENT_PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String refreshToken = JsonPath.read(loginBody, "$.refreshToken");

        mockMvc.perform(post("/auth/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s"}
                                """.formatted(user.getEmail())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "Caso o e-mail esteja cadastrado, as instruções serão enviadas."
                ));

        var captor = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        String rawToken = extractToken(captor.getValue().getText());
        PasswordResetToken persisted = passwordResetTokenRepository.findAll().getFirst();
        assertFalse(persisted.getTokenHash().equals(rawToken));
        assertTrue(persisted.getTokenHash().equals(secureTokenService.hash(rawToken)));

        mockMvc.perform(get("/auth/password/validate")
                        .param("token", rawToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        mockMvc.perform(post("/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token":"%s",
                                  "newPassword":"%s",
                                  "passwordConfirmation":"%s"
                                }
                                """.formatted(rawToken, NEW_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isOk());

        assertTrue(passwordEncoder.matches(
                NEW_PASSWORD,
                userRepository.findById(user.getId()).orElseThrow().getPassword()
        ));
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token":"%s",
                                  "newPassword":"AnotherPass@789",
                                  "passwordConfirmation":"AnotherPass@789"
                                }
                                """.formatted(rawToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredTokenIsRejectedAndUnknownEmailGetsGenericResponse() throws Exception {
        Admin user = createUser("expired@credential.test", false);
        String rawToken = secureTokenService.generate();
        passwordResetTokenRepository.saveAndFlush(new PasswordResetToken(
                user,
                secureTokenService.hash(rawToken),
                LocalDateTime.now().minusMinutes(1),
                "127.0.0.1"
        ));

        mockMvc.perform(post("/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token":"%s",
                                  "newPassword":"%s",
                                  "passwordConfirmation":"%s"
                                }
                                """.formatted(rawToken, NEW_PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("TOKEN_EXPIRED"));

        clearInvocations(mailSender);
        mockMvc.perform(post("/auth/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"unknown@credential.test"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "Caso o e-mail esteja cadastrado, as instruções serão enviadas."
                ));
        org.mockito.Mockito.verify(mailSender, org.mockito.Mockito.never())
                .send(any(SimpleMailMessage.class));
    }

    private Admin createUser(String email, boolean firstAccess) {
        Admin user = new Admin(
                "Credential User",
                email,
                passwordEncoder.encode(CURRENT_PASSWORD)
        );
        user.setUsername("credential." + UUID.randomUUID().toString().substring(0, 8));
        user.setOrganization(organization);
        user.setPasswordChangeRequired(firstAccess);
        if (firstAccess) {
            user.setTemporaryPasswordExpiresAt(LocalDateTime.now().plusDays(1));
        }
        return userRepository.saveAndFlush(user);
    }

    private String loginJson(String email, String password) {
        return """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);
    }

    private String extractToken(String text) {
        int start = text.indexOf("token=") + "token=".length();
        int end = text.indexOf('\n', start);
        return (end < 0 ? text.substring(start) : text.substring(start, end)).trim();
    }
}

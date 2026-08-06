package com.weg.Maintenance_API.user;

import com.jayway.jsonpath.JsonPath;
import com.weg.Maintenance_API.audit.repository.AuditLogRepository;
import com.weg.Maintenance_API.auth.password.repository.PasswordResetTokenRepository;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.OrganizationType;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.InvalidStateException;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.preference.repository.NotificationPreferenceRepository;
import com.weg.Maintenance_API.user.service.UserAccountFactory;
import com.weg.Maintenance_API.user.service.UserAdministrationService;
import org.junit.jupiter.api.AfterEach;
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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserAdministrationIntegrationTest {

    private static final String PASSWORD = "Administration@123";
    private static final AtomicInteger LOGIN_IP_SEQUENCE = new AtomicInteger(1);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private NotificationPreferenceRepository preferenceRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserAccountFactory userAccountFactory;
    @Autowired
    private UserAdministrationService administrationService;

    @MockitoBean
    private JavaMailSender mailSender;

    @AfterEach
    void cleanUp() {
        passwordResetTokenRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        preferenceRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
        clearInvocations(mailSender);
    }

    @Test
    void coordinatorOnlyDeactivatesStudentsAndTeachersAcrossOrganizations()
            throws Exception {
        Organization own = organization("Own", "own-admin.test");
        Organization other = organization("Other", "other-admin.test");
        User coordinator = user(Role.COORDENADOR, "coordinator@own-admin.test", own);
        User ownStudent = user(Role.ALUNO, "student@own-admin.test", own);
        User otherStudent = user(Role.ALUNO, "student@other-admin.test", other);
        User privileged = user(Role.ADMIN, "admin@own-admin.test", own);
        String coordinatorToken = login(coordinator.getEmail()).accessToken();
        Tokens ownStudentTokens = login(ownStudent.getEmail());

        mockMvc.perform(patch("/users/{id}/block", ownStudent.getId())
                        .header("Authorization", bearer(coordinatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reason("Bloqueio indevido")))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/users/{id}/unblock", ownStudent.getId())
                        .header("Authorization", bearer(coordinatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reason("Desbloqueio indevido")))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/users/{id}/reactivate", ownStudent.getId())
                        .header("Authorization", bearer(coordinatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reason("Reativação indevida")))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/users/{id}/role", ownStudent.getId())
                        .header("Authorization", bearer(coordinatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"PROFESSOR\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/users/{id}/deactivate", privileged.getId())
                        .header("Authorization", bearer(coordinatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reason("Tentativa privilegiada")))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/users/{id}/deactivate", ownStudent.getId())
                        .header("Authorization", bearer(coordinatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reason("Aluno desligado")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));
        mockMvc.perform(get("/users/me")
                        .header("Authorization", bearer(ownStudentTokens.accessToken())))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/users/{id}/deactivate", otherStudent.getId())
                        .header("Authorization", bearer(coordinatorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reason("Aluno de outra organização desligado")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));

        assertTrue(auditLogRepository.findAll().stream()
                .filter(log -> "USER_DEACTIVATED".equals(log.getAction()))
                .count() == 2);
    }

    @Test
    void adminUpdatesUserRegistrationDataAndStudentCannot() throws Exception {
        Organization organization = organization("Update", "update-admin.test");
        User admin = user(Role.ADMIN, "admin@update-admin.test", organization);
        User student = user(Role.ALUNO, "student@update-admin.test", organization);
        String adminToken = login(admin.getEmail()).accessToken();
        String studentToken = login(student.getEmail()).accessToken();
        String payload = """
                {
                  "name": "Aluno atualizado",
                  "email": "aluno.atualizado@update-admin.test",
                  "numberCard": "CARD-ATUALIZADO"
                }
                """;

        mockMvc.perform(put("/users/{id}", student.getId())
                        .header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/users/{id}", student.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aluno atualizado"))
                .andExpect(jsonPath("$.email").value("aluno.atualizado@update-admin.test"));

        User updated = userRepository.findById(student.getId()).orElseThrow();
        assertEquals("Aluno atualizado", updated.getName());
        assertEquals("CARD-ATUALIZADO", updated.getNumberCard());
    }

    @Test
    void managersListUsersAndAdminLoadsUserForEditing() throws Exception {
        Organization organization = organization("Listing", "listing-admin.test");
        User admin = user(Role.ADMIN, "admin@listing-admin.test", organization);
        User coordinator = user(Role.COORDENADOR, "coordinator@listing-admin.test", organization);
        User student = user(Role.ALUNO, "student@listing-admin.test", organization);
        String adminToken = login(admin.getEmail()).accessToken();
        String coordinatorToken = login(coordinator.getEmail()).accessToken();

        mockMvc.perform(get("/users")
                        .header("Authorization", bearer(coordinatorToken))
                        .queryParam("search", student.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(student.getId().toString()))
                .andExpect(jsonPath("$.content[0].numberCard").value(student.getNumberCard()))
                .andExpect(jsonPath("$.content[0].role").value("ALUNO"));

        mockMvc.perform(get("/users/{id}", student.getId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId().toString()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));

        mockMvc.perform(get("/users/{id}", admin.getId())
                        .header("Authorization", bearer(coordinatorToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminChangesRoleAndResendsCredentialsWithoutLeakingTemporaryPassword()
            throws Exception {
        Organization organization = organization("Global", "global-admin.test");
        User admin = user(Role.ADMIN, "admin@global-admin.test", organization);
        User student = user(Role.ALUNO, "student@global-admin.test", organization);
        String adminToken = login(admin.getEmail()).accessToken();
        Tokens studentTokens = login(student.getEmail());
        String previousPasswordHash = student.getPassword();
        long previousSecurityVersion = student.getSecurityVersion();

        mockMvc.perform(patch("/users/{id}/role", student.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"PROFESSOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PROFESSOR"));

        User changedRole = userRepository.findById(student.getId()).orElseThrow();
        assertTrue(changedRole instanceof Teacher);
        mockMvc.perform(get("/users/me")
                        .header("Authorization", bearer(studentTokens.accessToken())))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(studentTokens.refreshToken())))
                .andExpect(status().isUnauthorized());

        clearInvocations(mailSender);
        mockMvc.perform(post("/users/{id}/resend-credentials", student.getId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.userId").value(student.getId().toString()))
                .andExpect(jsonPath("$.emailStatus").value("PENDING"));

        User reissued = userRepository.findById(student.getId()).orElseThrow();
        assertNotEquals(previousPasswordHash, reissued.getPassword());
        assertTrue(reissued.isPasswordChangeRequired());
        assertTrue(reissued.getTemporaryPasswordExpiresAt() != null);
        assertTrue(reissued.getSecurityVersion() >= previousSecurityVersion + 2);
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertTrue(auditLogRepository.findAll().stream()
                .anyMatch(log -> "USER_ROLE_CHANGED".equals(log.getAction())));
        assertTrue(auditLogRepository.findAll().stream()
                .anyMatch(log -> "USER_CREDENTIALS_REISSUED".equals(log.getAction())));
        assertTrue(auditLogRepository.findAll().stream()
                .noneMatch(log -> log.getDetails() != null
                        && log.getDetails().contains(PASSWORD)));
    }

    @Test
    void selfRoleChangeAndRemovalOfLastActiveAdminAreRejected() throws Exception {
        Organization organization = organization("Safety", "safety-admin.test");
        User admin = user(Role.ADMIN, "admin@safety-admin.test", organization);
        String token = login(admin.getEmail()).accessToken();

        mockMvc.perform(patch("/users/{id}/role", admin.getId())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"COORDENADOR\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_STATE"));

        User disabledActor = user(Role.ADMIN, "disabled.admin@safety-admin.test", organization);
        disabledActor.setEnabled(false);
        userRepository.saveAndFlush(disabledActor);

        assertThrows(
                InvalidStateException.class,
                () -> administrationService.changeRole(
                        admin.getId(),
                        Role.COORDENADOR,
                        disabledActor.getEmail(),
                        new ClientRequestMetadata(
                                "/users/" + admin.getId() + "/role",
                                "PATCH",
                                "127.0.0.1",
                                "integration-test"
                        )
                )
        );
    }

    private Organization organization(String name, String domain) {
        return organizationRepository.saveAndFlush(new Organization(
                name + " " + UUID.randomUUID(),
                OrganizationType.OTHER,
                domain
        ));
    }

    private User user(Role role, String email, Organization organization) {
        User user = userAccountFactory.create(
                role.name().toLowerCase() + " user",
                role.name().toLowerCase() + "."
                        + UUID.randomUUID().toString().substring(0, 8),
                email,
                passwordEncoder.encode(PASSWORD),
                role,
                organization
        );
        user.setPasswordChangeRequired(false);
        return userRepository.saveAndFlush(user);
    }

    private Tokens login(String email) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .with(request -> {
                            request.setRemoteAddr(nextLoginAddress());
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return new Tokens(
                JsonPath.read(response, "$.accessToken"),
                JsonPath.read(response, "$.refreshToken")
        );
    }

    private String reason(String reason) {
        return """
                {"reason":"%s"}
                """.formatted(reason);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String nextLoginAddress() {
        return "198.51.100." + LOGIN_IP_SEQUENCE.getAndIncrement();
    }

    private record Tokens(String accessToken, String refreshToken) {
    }
}

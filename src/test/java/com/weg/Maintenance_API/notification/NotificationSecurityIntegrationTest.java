package com.weg.Maintenance_API.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.notification.entity.Notification;
import com.weg.Maintenance_API.notification.repository.NotificationRepository;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.entity.OrganizationType;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationSecurityIntegrationTest {

    private static final String FIRST_EMAIL = "first.notification@local.test";
    private static final String SECOND_EMAIL = "second.notification@local.test";
    private static final String PASSWORD = "ValidPass@123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID organizationId;
    private UUID firstUserId;
    private UUID secondUserId;
    private UUID firstNotificationId;
    private UUID secondNotificationId;

    @BeforeEach
    void setUp() {
        Organization organization = organizationRepository.saveAndFlush(
                new Organization(
                        "Notification Security " + UUID.randomUUID(),
                        OrganizationType.OTHER,
                        "local.test"
                )
        );
        organizationId = organization.getId();

        Student firstUser = createStudent(
                "First Notification User",
                FIRST_EMAIL,
                organization
        );
        Student secondUser = createStudent(
                "Second Notification User",
                SECOND_EMAIL,
                organization
        );
        firstUserId = firstUser.getId();
        secondUserId = secondUser.getId();

        Notification firstNotification = notificationRepository.saveAndFlush(
                new Notification(
                        FIRST_EMAIL,
                        "First",
                        "Security",
                        "First user's notification"
                )
        );
        Notification secondNotification = notificationRepository.saveAndFlush(
                new Notification(
                        SECOND_EMAIL,
                        "Second",
                        "Security",
                        "Second user's notification"
                )
        );
        firstNotificationId = firstNotification.getId();
        secondNotificationId = secondNotification.getId();
    }

    @AfterEach
    void cleanUp() {
        refreshTokenRepository.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteById(firstUserId);
        userRepository.deleteById(secondUserId);
        organizationRepository.deleteById(organizationId);
    }

    @Test
    void userOnlyReadsAndChangesOwnNotifications() throws Exception {
        String accessToken = login(FIRST_EMAIL);

        mockMvc.perform(get("/notification")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value(FIRST_EMAIL));

        mockMvc.perform(get("/notification/{id}", secondNotificationId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/notification/read-all")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        org.junit.jupiter.api.Assertions.assertTrue(
                notificationRepository.findById(firstNotificationId)
                        .orElseThrow()
                        .isStatusRead()
        );
        org.junit.jupiter.api.Assertions.assertFalse(
                notificationRepository.findById(secondNotificationId)
                        .orElseThrow()
                        .isStatusRead()
        );
    }

    @Test
    void studentCannotSendArbitraryNotificationEmail() throws Exception {
        String accessToken = login(FIRST_EMAIL);

        mockMvc.perform(post("/notification")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "external@example.com",
                                  "title": "Unauthorized",
                                  "about": "Security",
                                  "description": "Must not be sent"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    private Student createStudent(
            String name,
            String email,
            Organization organization
    ) {
        Student student = new Student(
                name,
                email,
                passwordEncoder.encode(PASSWORD),
                UUID.randomUUID().toString()
        );
        student.setUsername(
                "notification." + UUID.randomUUID().toString().substring(0, 8)
        );
        student.setOrganization(organization);
        student.setPasswordChangeRequired(false);
        return (Student) userRepository.saveAndFlush(student);
    }

    private String login(String email) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.accessToken");
    }
}

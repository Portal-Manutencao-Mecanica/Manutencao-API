package com.weg.Maintenance_API.user;

import com.jayway.jsonpath.JsonPath;
import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.repository.AuditLogRepository;
import com.weg.Maintenance_API.auth.password.repository.PasswordResetTokenRepository;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.media.repository.MediaRepository;
import com.weg.Maintenance_API.media.storage.FileStorageService;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.entity.OrganizationType;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.preference.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.file-storage.path=target/test-profile-storage"
})
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
    private MediaRepository mediaRepository;
    @Autowired
    private FileStorageService fileStorageService;
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
        var users = userRepository.findAll();
        users.forEach(user -> user.setProfilePhoto(null));
        userRepository.saveAllAndFlush(users);
        mediaRepository.findAll().forEach(media -> {
            fileStorageService.delete(media.getImage());
            mediaRepository.delete(media);
        });
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

    @Test
    void profilePhotoUsesValidatedGeneratedStorageKey() throws Exception {
        byte[] png = {
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x01
        };
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../avatar.png",
                "image/png",
                png
        );

        mockMvc.perform(multipart("/users/me/photo")
                        .file(file)
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentType").value("image/png"))
                .andExpect(jsonPath("$.originalFilename").value("avatar.png"));

        var media = mediaRepository.findAll().getFirst();
        assertTrue(media.getImage().startsWith("profile/"));
        assertTrue(fileStorageService.load(media.getImage()).exists());
    }

    @Test
    void profilePhotoRejectsMismatchedContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "not-an-image".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/users/me/photo")
                        .file(file)
                        .header("Authorization", bearer()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_FILE"));
    }

    private String bearer() {
        return "Bearer " + accessToken;
    }
}

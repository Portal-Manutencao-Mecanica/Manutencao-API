package com.weg.Maintenance_API.userimport;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.audit.repository.AuditLogRepository;
import com.weg.Maintenance_API.auth.repository.RefreshTokenRepository;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.enums.OrganizationType;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.userimport.dto.UserImportResponse;
import com.weg.Maintenance_API.enums.UserImportItemStatus;
import com.weg.Maintenance_API.enums.UserImportStatus;
import com.weg.Maintenance_API.userimport.repository.UserImportItemRepository;
import com.weg.Maintenance_API.userimport.repository.UserImportRepository;
import com.weg.Maintenance_API.userimport.service.UserImportService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserImportIntegrationTest {

    private static final String CSV = "text/csv";
    private static final String XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired
    private UserImportService userImportService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserImportRepository userImportRepository;
    @Autowired
    private UserImportItemRepository itemRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JavaMailSender mailSender;

    @AfterEach
    void cleanUp() {
        itemRepository.deleteAll();
        userImportRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    void adminImportsEveryRoleFromCsv() throws Exception {
        Organization organization = organization("Local", "local.test");
        Admin admin = admin(organization);

        MockMultipartFile file = csv(List.<String[]>of(
                new String[]{
                        "Professor Teste", "prof@local.test",
                        "PROFESSOR", "OTHER"
                },
                new String[]{
                        "Admin Indevido", "admin2@local.test",
                        "ADMIN", "OTHER"
                }
        ));

        UserImportResponse response = userImportService.importUsers(
                file,
                admin.getEmail(),
                metadata()
        );

        assertEquals(UserImportStatus.COMPLETED, response.status());
        assertEquals(2, response.created());
        assertEquals(0, response.failed());
        assertEquals(UserImportItemStatus.CREATED, response.items().get(0).status());
        assertEquals(UserImportItemStatus.CREATED, response.items().get(1).status());
        var imported = userRepository.findByEmailIgnoreCase("prof@local.test").orElseThrow();
        assertEquals("professor.teste1", imported.getUsername());
        assertTrue(imported.isPasswordChangeRequired());
        assertFalse(imported.getPassword().isBlank());
        assertTrue(imported.getPassword().startsWith("$2"));
    }

    @Test
    void coordinatorCanImportTeacherOrStudentIntoAnotherOrganization() throws Exception {
        Organization own = organization("Own", OrganizationType.SENAI, "own.test");
        Organization other = organization("Other", OrganizationType.WEG, "other.test");
        Coordinator coordinator = new Coordinator(
                "Coordenador",
                "coord@own.test",
                passwordEncoder.encode("ValidPass@123")
        );
        coordinator.setUsername("coordenador");
        coordinator.setOrganization(own);
        userRepository.saveAndFlush(coordinator);

        MockMultipartFile file = workbook(List.<String[]>of(
                new String[]{
                        "Aluno", "aluno@other.test",
                        "ALUNO", "WEG"
                }
        ));

        UserImportResponse response = userImportService.importUsers(
                file,
                coordinator.getEmail(),
                metadata()
        );

        assertEquals(1, response.created());
        assertEquals(0, response.failed());
        assertTrue(userRepository.existsByEmailIgnoreCase("aluno@other.test"));
    }

    @Test
    void adminRejectsInvalidOrganizationType() {
        Organization organization = organization("Local", "local.test");
        Admin admin = admin(organization);
        MockMultipartFile file = csv(List.<String[]>of(new String[]{
                "Aluno", "aluno@local.test", "ALUNO", "ACME"
        }));

        UserImportResponse response = userImportService.importUsers(
                file,
                admin.getEmail(),
                metadata()
        );

        assertEquals(0, response.created());
        assertEquals(1, response.failed());
        assertEquals("INVALID_ORGANIZATION", response.items().getFirst().errorCode());
        assertEquals(
                "Organização inválida. Valores permitidos: SENAI, WEG ou OTHER.",
                response.items().getFirst().message()
        );
    }

    @Test
    void coordinatorCannotImportAdministrator() throws Exception {
        Organization own = organization("Own", OrganizationType.SENAI, "own.test");
        Coordinator coordinator = new Coordinator(
                "Coordenador",
                "coord@own.test",
                passwordEncoder.encode("ValidPass@123")
        );
        coordinator.setUsername("coordenador");
        coordinator.setOrganization(own);
        userRepository.saveAndFlush(coordinator);

        MockMultipartFile file = csv(List.<String[]>of(
                new String[]{
                        "Admin Indevido", "admin@own.test",
                        "ADMIN", "SENAI"
                }
        ));

        UserImportResponse response = userImportService.importUsers(
                file,
                coordinator.getEmail(),
                metadata()
        );

        assertEquals(0, response.created());
        assertEquals(1, response.failed());
        assertEquals("ACCESS_DENIED", response.items().getFirst().errorCode());
        assertEquals("role", response.items().getFirst().field());
    }

    @Test
    void duplicateEmailsInsideFileFailEveryDuplicateRow() throws Exception {
        Organization organization = organization("Duplicate", "dup.test");
        Admin admin = admin(organization);
        MockMultipartFile file = workbook(List.<String[]>of(
                new String[]{
                        "Aluno Um", "duplicado@dup.test",
                        "ALUNO", "OTHER"
                },
                new String[]{
                        "Aluno Dois", "duplicado@dup.test",
                        "ALUNO", "OTHER"
                }
        ));

        UserImportResponse response = userImportService.importUsers(
                file,
                admin.getEmail(),
                metadata()
        );

        assertEquals(0, response.created());
        assertEquals(2, response.failed());
        assertTrue(response.items().stream()
                .allMatch(item -> "DUPLICATE_EMAIL_IN_FILE".equals(item.errorCode())));
    }

    @Test
    void importsSameFullNameWithSequentialGeneratedUsernames() {
        Organization organization = organization("Sequential", "sequential.test");
        Admin admin = admin(organization);
        MockMultipartFile file = csv(List.of(
                new String[]{"Junior da Silva", "junior1@sequential.test", "ALUNO", "OTHER"},
                new String[]{"Junior da Silva", "junior2@sequential.test", "ALUNO", "OTHER"},
                new String[]{"Junior Souza", "junior.souza@sequential.test", "ALUNO", "OTHER"}
        ));

        UserImportResponse response = userImportService.importUsers(
                file,
                admin.getEmail(),
                metadata()
        );

        assertEquals(3, response.created());
        assertEquals("junior.da.silva1", userRepository
                .findByEmailIgnoreCase("junior1@sequential.test")
                .orElseThrow()
                .getUsername());
        assertEquals("junior.da.silva2", userRepository
                .findByEmailIgnoreCase("junior2@sequential.test")
                .orElseThrow()
                .getUsername());
        assertEquals("junior.souza1", userRepository
                .findByEmailIgnoreCase("junior.souza@sequential.test")
                .orElseThrow()
                .getUsername());
    }

    private Organization organization(String name, String domain) {
        return organization(name, OrganizationType.OTHER, domain);
    }

    private Organization organization(String name, OrganizationType type, String domain) {
        return organizationRepository.saveAndFlush(
                new Organization(name + UUID.randomUUID(), type, domain)
        );
    }

    private Admin admin(Organization organization) {
        Admin admin = new Admin(
                "Administrador",
                "admin@" + organization.getEmailDomain(),
                passwordEncoder.encode("ValidPass@123")
        );
        admin.setUsername("admin." + UUID.randomUUID().toString().substring(0, 8));
        admin.setOrganization(organization);
        return userRepository.saveAndFlush(admin);
    }

    private MockMultipartFile workbook(List<String[]> rows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("users");
            var header = sheet.createRow(0);
            String[] headers = {"name", "email", "role", "organization", "classGroupIds"};
            for (int index = 0; index < headers.length; index++) {
                header.createCell(index).setCellValue(headers[index]);
            }
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                var row = sheet.createRow(rowIndex + 1);
                String[] values = rows.get(rowIndex);
                for (int column = 0; column < values.length; column++) {
                    row.createCell(column).setCellValue(values[column]);
                }
            }
            workbook.write(output);
            return new MockMultipartFile(
                    "file",
                    "users.xlsx",
                    XLSX,
                    output.toByteArray()
            );
        }
    }

    private MockMultipartFile csv(List<String[]> rows) {
        StringBuilder content = new StringBuilder("name,email,role,organization,classGroupIds\n");
        for (String[] row : rows) {
            content.append(String.join(",", row)).append('\n');
        }
        return new MockMultipartFile(
                "file",
                "users.csv",
                CSV,
                content.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    private ClientRequestMetadata metadata() {
        return new ClientRequestMetadata(
                "/users/import",
                "POST",
                "127.0.0.1",
                "JUnit"
        );
    }
}

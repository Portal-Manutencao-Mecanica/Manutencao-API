package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.admin.entity.Admin;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.enums.OrganizationType;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import com.weg.Maintenance_API.user.dto.request.StudentDataRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserCreationServiceTransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserCreationService userCreationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserAccountFactory userAccountFactory;

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    void rollsBackUserAndProfileWhenARequestedClassGroupDoesNotExist() {
        Organization organization = organizationRepository.saveAndFlush(
                new Organization("Local", OrganizationType.OTHER, "local.test")
        );
        Admin actor = (Admin) userAccountFactory.create(
                "Admin",
                "admin.local",
                "admin@local.test",
                "hash",
                Role.ADMIN,
                organization
        );
        userRepository.saveAndFlush(actor);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(actor.getEmail(), null, "ROLE_ADMIN")
        );

        String newEmail = "student@local.test";
        assertThrows(ResourceNotFoundException.class, () -> userCreationService.create(
                new CreateUserRequest(
                        "Student",
                        "student.local",
                        newEmail,
                        Role.ALUNO,
                        organization.getId(),
                        new StudentDataRequest(List.of(UUID.randomUUID())),
                        null
                ),
                new ClientRequestMetadata("/users", "POST", "127.0.0.1", "JUnit")
        ));

        assertFalse(userRepository.existsByEmailIgnoreCase(newEmail));
    }

    @Test
    void postUsersIsNotPermitAll() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerDocumentsSeparateProfileExamplesWithoutCoordinatorData() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"ALUNO\"")))
                .andExpect(content().string(containsString("\"PROFESSOR\"")))
                .andExpect(content().string(containsString("\"COORDENADOR\"")))
                .andExpect(content().string(containsString("\"ADMIN\"")))
                .andExpect(content().string(not(containsString("coordinatorData"))));
    }
}

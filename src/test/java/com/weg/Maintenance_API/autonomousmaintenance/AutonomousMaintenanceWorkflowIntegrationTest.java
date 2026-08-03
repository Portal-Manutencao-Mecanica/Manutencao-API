package com.weg.Maintenance_API.autonomousmaintenance;

import tools.jackson.databind.ObjectMapper;
import com.weg.Maintenance_API.auth.service.JwtTokenService;
import com.weg.Maintenance_API.autonomousmaintenance.entity.AutonomousMaintenance;
import com.weg.Maintenance_API.autonomousmaintenance.repository.AutonomousMaintenanceRepository;
import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.enums.EquipmentCondition;
import com.weg.Maintenance_API.enums.OrganizationType;
import com.weg.Maintenance_API.event.repository.EventRepository;
import com.weg.Maintenance_API.machine.entity.Machine;
import com.weg.Maintenance_API.machine.repository.MachineRepository;
import com.weg.Maintenance_API.notification.repository.NotificationRepository;
import com.weg.Maintenance_API.organization.entity.Organization;
import com.weg.Maintenance_API.organization.repository.OrganizationRepository;
import com.weg.Maintenance_API.place.entity.Place;
import com.weg.Maintenance_API.place.repository.PlaceRepository;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutonomousMaintenanceWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private AutonomousMaintenanceRepository maintenanceRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private JwtTokenService jwtTokenService;

    private Organization organization;
    private Organization otherOrganization;
    private Teacher teacher;
    private Coordinator coordinator;
    private Coordinator otherCoordinator;
    private Student firstStudent;
    private Student secondStudent;
    private Student unassignedStudent;
    private Student otherOrganizationStudent;
    private Machine machine;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        organization = organizationRepository.save(new Organization(
                "Autonomous " + suffix, OrganizationType.OTHER, "auto-" + suffix + ".test"));
        otherOrganization = organizationRepository.save(new Organization(
                "Other " + suffix, OrganizationType.OTHER, "other-" + suffix + ".test"));

        teacher = saveUser(new Teacher(), "Professor", "teacher", organization);
        coordinator = saveUser(new Coordinator(), "Coordenador", "coordinator", organization);
        otherCoordinator = saveUser(
                new Coordinator(), "Outro coordenador", "other-coordinator", otherOrganization);
        firstStudent = saveUser(new Student(), "Aluno um", "student-one", organization);
        secondStudent = saveUser(new Student(), "Aluno dois", "student-two", organization);
        unassignedStudent = saveUser(new Student(), "Aluno tres", "student-three", organization);
        otherOrganizationStudent = saveUser(
                new Student(), "Aluno externo", "external-student", otherOrganization);

        Place place = new Place();
        place.setName("Laboratorio " + suffix);
        place = placeRepository.save(place);

        machine = new Machine();
        machine.setName("Torno CNC " + suffix);
        machine.setPatrimony("PAT-" + suffix);
        machine.setCondition(EquipmentCondition.CONFORME);
        machine.setPlace(place);
        machine = machineRepository.save(machine);
    }

    @AfterEach
    void clearSecurityContext() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void onlyTeacherCreatesAndResponsibleTeacherCannotBeForged() throws Exception {
        String validBody = createBody(firstStudent.getId(), secondStudent.getId());

        mockMvc.perform(post("/manutencao-autonoma")
                        .header("Authorization", bearer(firstStudent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/manutencao-autonoma")
                        .header("Authorization", bearer(coordinator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isForbidden());

        Map<String, Object> forgedBody = objectMapper.readValue(validBody, Map.class);
        forgedBody.put("responsibleTeacherId", otherCoordinator.getId().toString());
        mockMvc.perform(post("/manutencao-autonoma")
                        .header("Authorization", bearer(teacher))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgedBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void teacherCreatesForSeveralStudentsAndDuplicatesAreNormalized() throws Exception {
        String response = mockMvc.perform(post("/manutencao-autonoma")
                        .header("Authorization", bearer(teacher))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(
                                firstStudent.getId(),
                                secondStudent.getId(),
                                firstStudent.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responsibleTeacherId")
                        .value(teacher.getId().toString()))
                .andExpect(jsonPath("$.students", hasSize(2)))
                .andExpect(jsonPath("$.status")
                        .value("PENDENTE_APROVACAO_COORDENADOR"))
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());
        AutonomousMaintenance persisted = maintenanceRepository.findById(id).orElseThrow();
        assertEquals(teacher.getId(), persisted.getCreatedBy().getId());
        assertEquals(2, persisted.getAssignedStudents().size());
        assertEquals(1, notificationRepository
                .countByEmailIgnoreCaseAndStatusReadFalse(coordinator.getEmail()));
    }

    @Test
    void invalidStudentIsRejected() throws Exception {
        firstStudent.setEnabled(false);
        userRepository.save(firstStudent);

        mockMvc.perform(post("/manutencao-autonoma")
                        .header("Authorization", bearer(teacher))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(firstStudent.getId())))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/manutencao-autonoma")
                        .header("Authorization", bearer(teacher))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(otherOrganizationStudent.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void coordinatorApprovesOnceCreatesEventAndControlsStudentVisibility() throws Exception {
        UUID maintenanceId = createMaintenance(firstStudent.getId(), secondStudent.getId());

        mockMvc.perform(get("/manutencao-autonoma/{id}", maintenanceId)
                        .header("Authorization", bearer(firstStudent)))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/manutencao-autonoma/{id}/aprovacao", maintenanceId)
                        .header("Authorization", bearer(otherCoordinator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approved\":true,\"reason\":null}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/manutencao-autonoma/{id}/aprovacao", maintenanceId)
                        .header("Authorization", bearer(coordinator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approved\":true,\"reason\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA_PELO_COORDENADOR"))
                .andExpect(jsonPath("$.calendarEventId").isNotEmpty());

        AutonomousMaintenance approved = maintenanceRepository.findById(maintenanceId).orElseThrow();
        assertNotNull(approved.getCoordinatorApprover());
        assertNotNull(approved.getApprovedAt());
        assertNotNull(approved.getCalendarEvent());
        assertEquals(1, eventRepository.count());
        assertEquals(1, notificationRepository
                .countByEmailIgnoreCaseAndStatusReadFalse(teacher.getEmail()));
        assertEquals(1, notificationRepository
                .countByEmailIgnoreCaseAndStatusReadFalse(firstStudent.getEmail()));
        assertEquals(1, notificationRepository
                .countByEmailIgnoreCaseAndStatusReadFalse(secondStudent.getEmail()));

        mockMvc.perform(patch("/manutencao-autonoma/{id}/aprovacao", maintenanceId)
                        .header("Authorization", bearer(coordinator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approved\":true,\"reason\":null}"))
                .andExpect(status().isUnprocessableEntity());
        assertEquals(1, eventRepository.count());

        mockMvc.perform(get("/manutencao-autonoma/{id}", maintenanceId)
                        .header("Authorization", bearer(firstStudent)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/manutencao-autonoma/{id}", maintenanceId)
                        .header("Authorization", bearer(unassignedStudent)))
                .andExpect(status().isForbidden());
        assertTrue(eventRepository.findAllForCalendar().stream()
                .anyMatch(event -> event.getId().equals(approved.getCalendarEvent().getId())));
    }

    @Test
    void rejectionRequiresReasonAndNotifiesTeacherWithReason() throws Exception {
        UUID maintenanceId = createMaintenance(firstStudent.getId());

        mockMvc.perform(patch("/manutencao-autonoma/{id}/aprovacao", maintenanceId)
                        .header("Authorization", bearer(coordinator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approved\":false,\"reason\":\"  \"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/manutencao-autonoma/{id}/aprovacao", maintenanceId)
                        .header("Authorization", bearer(coordinator))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approved\":false,\"reason\":\"Falta detalhamento\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPROVADA_PELO_COORDENADOR"))
                .andExpect(jsonPath("$.rejectionReason").value("Falta detalhamento"));

        assertTrue(notificationRepository
                .findAllByEmailIgnoreCaseOrderByIdDesc(
                        teacher.getEmail(), org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .anyMatch(notification -> notification.getDescription().contains("Falta detalhamento")));
    }

    private UUID createMaintenance(UUID... studentIds) throws Exception {
        String response = mockMvc.perform(post("/manutencao-autonoma")
                        .header("Authorization", bearer(teacher))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(studentIds)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    private String createBody(UUID... studentIds) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "equipmentSituation", "OPERANDO",
                "scheduledFor", LocalDateTime.now().plusDays(7).withNano(0).toString(),
                "inspectedMachineId", machine.getId().toString(),
                "equipmentCondition", "CONFORME",
                "identifiedNonconformities", "Nenhuma nao conformidade.",
                "studentIds", studentIds
        ));
    }

    private String bearer(User user) {
        return "Bearer " + jwtTokenService.generateToken(user).accessToken();
    }

    @SuppressWarnings("unchecked")
    private <T extends User> T saveUser(
            T user,
            String name,
            String usernamePrefix,
            Organization userOrganization
    ) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        user.setName(name);
        user.setUsername(usernamePrefix + "." + suffix);
        user.setEmail(usernamePrefix + "." + suffix + "@" + userOrganization.getEmailDomain());
        user.setPassword("not-used-in-this-test");
        user.setNumberCard("CARD-" + UUID.randomUUID());
        user.setOrganization(userOrganization);
        user.setPasswordChangeRequired(false);
        return (T) userRepository.save(user);
    }
}

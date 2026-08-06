package com.weg.Maintenance_API.buy.service;

import com.weg.Maintenance_API.buy.dto.request.BuyDtoRequest;
import com.weg.Maintenance_API.buy.dto.request.BuyItemRequest;
import com.weg.Maintenance_API.buy.dto.request.BuyPatchRequest;
import com.weg.Maintenance_API.buy.entity.Buy;
import com.weg.Maintenance_API.buy.entity.BuyItem;
import com.weg.Maintenance_API.buy.mapper.BuyMapper;
import com.weg.Maintenance_API.buy.mapper.BuyItemMapper;
import com.weg.Maintenance_API.buy.repository.BuyRepository;
import com.weg.Maintenance_API.classgroup.entity.ClassGroup;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.enums.BuyStatus;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.service.EntityReferenceService;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.user.service.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class BuyServiceTest {

    @Mock
    private BuyRepository repository;
    @Mock
    private BuyMapper mapper;
    @Mock
    private BuyItemMapper itemMapper;
    @Mock
    private EntityReferenceService references;
    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private BuyService service;

    @Test
    void createAssociatesAuthenticatedUserAndRequestReferences() {
        UUID classGroupId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        UUID equipmentId = UUID.randomUUID();
        BuyDtoRequest request = new BuyDtoRequest(
                "Ferramentas necessarias para a aula",
                classGroupId,
                teacherId,
                List.of(new BuyItemRequest(
                        equipmentId,
                        2,
                        "Aco rapido",
                        "SAP-123",
                        "PAT-123",
                        "TAG-123",
                        "Conjunto principal"
                )),
                List.of()
        );
        Buy buy = new Buy();
        BuyItem item = new BuyItem();
        buy.setItems(new ArrayList<>(List.of(item)));
        Student authenticatedUser = new Student();
        ClassGroup classGroup = new ClassGroup();
        Teacher teacher = new Teacher();
        Equipment equipment = new Equipment();
        equipment.setSap("SAP-CADASTRADO");
        equipment.setPatrimony("PAT-CADASTRADO");
        equipment.setTag("TAG-CADASTRADA");

        when(mapper.toEntity(request)).thenReturn(buy);
        when(itemMapper.toEntity(request.items().getFirst())).thenReturn(item);
        when(authenticatedUserService.requireCurrentUser()).thenReturn(authenticatedUser);
        when(references.classGroup(classGroupId)).thenReturn(classGroup);
        when(references.teacher(teacherId)).thenReturn(teacher);
        when(references.equipment(equipmentId)).thenReturn(equipment);

        service.create(request);

        assertThat(buy.getCreatedBy()).isSameAs(authenticatedUser);
        assertThat(buy.getClassGroup()).isSameAs(classGroup);
        assertThat(buy.getNotifiedTeacher()).isSameAs(teacher);
        assertThat(item.getEquipment()).isSameAs(equipment);
        assertThat(item.getSap()).isEqualTo("SAP-CADASTRADO");
        assertThat(item.getPatrimony()).isEqualTo("PAT-CADASTRADO");
        assertThat(item.getTag()).isEqualTo("TAG-CADASTRADA");
        assertThat(item.getBuy()).isSameAs(buy);
        verify(repository).save(buy);
    }

    @Test
    void notifiedTeacherFirstViewMovesStudentRequestToAnalysis() {
        UUID buyId = UUID.randomUUID();
        Teacher notifiedTeacher = new Teacher();
        notifiedTeacher.setId(UUID.randomUUID());
        Student creator = new Student();
        creator.setId(UUID.randomUUID());
        Buy buy = new Buy();
        buy.setId(buyId);
        buy.setCreatedBy(creator);
        buy.setNotifiedTeacher(notifiedTeacher);
        buy.setStatus(BuyStatus.NAO_VISUALIZADO);

        when(repository.findByIdForUpdate(buyId)).thenReturn(Optional.of(buy));
        when(authenticatedUserService.requireCurrentUser()).thenReturn(notifiedTeacher);

        service.getById(buyId);

        assertThat(buy.getStatus()).isEqualTo(BuyStatus.EM_ANALISE);
        verify(repository).save(buy);
    }

    @Test
    void anotherTeacherViewDoesNotConsumeAuthorsEditWindow() {
        UUID buyId = UUID.randomUUID();
        Teacher notifiedTeacher = new Teacher();
        notifiedTeacher.setId(UUID.randomUUID());
        Teacher anotherTeacher = new Teacher();
        anotherTeacher.setId(UUID.randomUUID());
        anotherTeacher.setRole(Role.PROFESSOR);
        Student creator = new Student();
        creator.setId(UUID.randomUUID());
        Buy buy = new Buy();
        buy.setId(buyId);
        buy.setCreatedBy(creator);
        buy.setNotifiedTeacher(notifiedTeacher);
        buy.setStatus(BuyStatus.NAO_VISUALIZADO);

        when(repository.findByIdForUpdate(buyId)).thenReturn(Optional.of(buy));
        when(authenticatedUserService.requireCurrentUser()).thenReturn(anotherTeacher);

        service.getById(buyId);

        assertThat(buy.getStatus()).isEqualTo(BuyStatus.NAO_VISUALIZADO);
    }

    @Test
    void viewedPurchaseContentCannotBeChangedEvenByManager() {
        UUID buyId = UUID.randomUUID();
        Student creator = new Student();
        creator.setId(UUID.randomUUID());
        Teacher manager = new Teacher();
        manager.setId(UUID.randomUUID());
        manager.setRole(Role.ADMIN);
        Buy buy = new Buy();
        buy.setId(buyId);
        buy.setCreatedBy(creator);
        buy.setStatus(BuyStatus.EM_ANALISE);

        when(repository.findById(buyId)).thenReturn(Optional.of(buy));
        when(authenticatedUserService.requireCurrentUser()).thenReturn(manager);

        assertThatThrownBy(() -> service.patch(
                buyId,
                new BuyPatchRequest("Justificativa alterada", null)
        )).isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("antes da primeira visualização");
    }

    @Test
    void managerCanChangeStatusWithoutChangingViewedContent() {
        UUID buyId = UUID.randomUUID();
        Student creator = new Student();
        creator.setId(UUID.randomUUID());
        Teacher manager = new Teacher();
        manager.setId(UUID.randomUUID());
        manager.setRole(Role.COORDENADOR);
        Buy buy = new Buy();
        buy.setId(buyId);
        buy.setCreatedBy(creator);
        buy.setStatus(BuyStatus.EM_ANALISE);

        when(repository.findById(buyId)).thenReturn(Optional.of(buy));
        when(authenticatedUserService.requireCurrentUser()).thenReturn(manager);

        service.patch(buyId, new BuyPatchRequest(null, "ENTREGUE"));

        assertThat(buy.getStatus()).isEqualTo(BuyStatus.ENTREGUE);
        verify(repository).save(buy);
    }
}

package com.weg.Maintenance_API.search;

import com.weg.Maintenance_API.buy.mapper.BuyItemMapper;
import com.weg.Maintenance_API.buy.mapper.BuyMapper;
import com.weg.Maintenance_API.buy.repository.BuyRepository;
import com.weg.Maintenance_API.buy.service.BuyService;
import com.weg.Maintenance_API.classgroup.mapper.ClassGroupMapper;
import com.weg.Maintenance_API.classgroup.repository.ClassGroupRepository;
import com.weg.Maintenance_API.classgroup.service.ClassGroupService;
import com.weg.Maintenance_API.equipment.mapper.EquipmentMapper;
import com.weg.Maintenance_API.equipment.repository.EquipmentRepository;
import com.weg.Maintenance_API.equipment.service.EquipmentService;
import com.weg.Maintenance_API.machine.mapper.MachineMapper;
import com.weg.Maintenance_API.machine.repository.MachineRepository;
import com.weg.Maintenance_API.machine.service.MachineService;
import com.weg.Maintenance_API.service.EntityReferenceService;
import com.weg.Maintenance_API.student.mapper.StudentMapper;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.student.service.StudentService;
import com.weg.Maintenance_API.teacher.repository.TeacherRepository;
import com.weg.Maintenance_API.user.service.AuthenticatedUserService;
import com.weg.Maintenance_API.user.service.UserIdentityPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OptionalSearchFilterTest {

    private final Pageable pageable = PageRequest.of(0, 5);

    @Test
    void listServicesUseEmptySearchWhenTheFilterIsAbsent() {
        MachineRepository machineRepository = mock(MachineRepository.class);
        ClassGroupRepository classGroupRepository = mock(ClassGroupRepository.class);
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        BuyRepository buyRepository = mock(BuyRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);

        when(machineRepository.findAllFiltered("", null, pageable)).thenReturn(Page.empty());
        when(classGroupRepository.findAllFiltered("", null, pageable)).thenReturn(Page.empty());
        when(equipmentRepository.findAllFiltered("", pageable)).thenReturn(Page.empty());
        when(buyRepository.findAllFiltered("", null, pageable)).thenReturn(Page.empty());
        when(studentRepository.findAllFiltered("", null, pageable)).thenReturn(Page.empty());

        new MachineService(
                mock(MachineMapper.class), machineRepository, mock(EntityReferenceService.class)
        ).getAll(null, null, pageable);
        new ClassGroupService(
                classGroupRepository,
                mock(ClassGroupMapper.class),
                mock(TeacherRepository.class),
                studentRepository
        ).getAll(null, null, pageable);
        new EquipmentService(mock(EquipmentMapper.class), equipmentRepository).getAll(null, pageable);
        new BuyService(
                buyRepository,
                mock(BuyMapper.class),
                mock(BuyItemMapper.class),
                mock(EntityReferenceService.class),
                mock(AuthenticatedUserService.class)
        ).getAll(null, null, pageable);
        new StudentService(
                studentRepository,
                mock(StudentMapper.class),
                mock(PasswordEncoder.class),
                mock(UserIdentityPolicy.class)
        ).getAll(null, null, pageable);

        verify(machineRepository).findAllFiltered("", null, pageable);
        verify(classGroupRepository).findAllFiltered("", null, pageable);
        verify(equipmentRepository).findAllFiltered("", pageable);
        verify(buyRepository).findAllFiltered("", null, pageable);
        verify(studentRepository).findAllFiltered("", null, pageable);
    }
}
package com.weg.Maintenance_API.equipment.service;

import com.weg.Maintenance_API.equipment.dto.request.EquipmentRequest;
import com.weg.Maintenance_API.equipment.entity.Equipment;
import com.weg.Maintenance_API.equipment.mapper.EquipmentMapper;
import com.weg.Maintenance_API.equipment.repository.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService service;

    @Test
    void saveGeneratesEquipmentIdentifiersAndIgnoresClientValues() {
        Equipment equipment = new Equipment();
        EquipmentRequest request = requestWithIdentifiers();

        when(equipmentMapper.toEntity(request)).thenReturn(equipment);
        when(equipmentRepository.save(equipment)).thenReturn(equipment);

        service.save(request);

        assertThat(equipment.getSap()).matches("SAP-[A-F0-9]{12}");
        assertThat(equipment.getPatrimony()).matches("PAT-[A-F0-9]{12}");
        assertThat(equipment.getTag()).matches("TAG-[A-F0-9]{12}");
        assertThat(equipment.getSap()).isNotEqualTo("SAP-123");
    }

    @Test
    void updatePreservesAutomaticEquipmentIdentifiers() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment();
        equipment.setSap("SAP-EXISTENTE");
        equipment.setPatrimony("PAT-EXISTENTE");
        equipment.setTag("TAG-EXISTENTE");
        EquipmentRequest request = requestWithIdentifiers();

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));

        service.update(equipmentId, request);

        assertThat(equipment.getName()).isEqualTo("Chave de boca 22 mm");
        assertThat(equipment.getSap()).isEqualTo("SAP-EXISTENTE");
        assertThat(equipment.getPatrimony()).isEqualTo("PAT-EXISTENTE");
        assertThat(equipment.getTag()).isEqualTo("TAG-EXISTENTE");
        verify(equipmentRepository).save(equipment);
    }

    private EquipmentRequest requestWithIdentifiers() {
        return new EquipmentRequest(
                "Chave de boca 22 mm",
                "SAP-123",
                "PAT-456",
                "FERR-022",
                new BigDecimal("49.90"),
                10
        );
    }
}

package com.weg.Manutencao_API.manutencaoautonoma.mapper;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.equipamento.entity.Equipment;
import com.weg.Manutencao_API.manutencaoautonoma.dto.requests.AutonomousMaintenanceDtoRequest;
import com.weg.Manutencao_API.manutencaoautonoma.dto.response.AutonomousMaintenanceDtoResponse;
import com.weg.Manutencao_API.manutencaoautonoma.entity.AutonomousMaintenance;
import com.weg.Manutencao_API.professor.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AutonomousMaintenanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inspectedEquipment", source = "equipmentId", qualifiedByName = "equipmentFromId")
    @Mapping(target = "teacher", source = "teacherId", qualifiedByName = "teacherFromId")
    @Mapping(target = "student", source = "studentId", qualifiedByName = "studentFromId")
    AutonomousMaintenance toEntity(AutonomousMaintenanceDtoRequest request);

    @Mapping(target = "equipmentId", source = "inspectedEquipment.id")
    @Mapping(target = "equipmentName", source = "inspectedEquipment.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    AutonomousMaintenanceDtoResponse toResponse(AutonomousMaintenance autonomousMaintenance);

    @Named("equipmentFromId")
    default Equipment equipmentFromId(Long id) {
        if (id == null) {
            return null;
        }

        Equipment equipment = new Equipment();
        equipment.setId(id);
        return equipment;
    }

    @Named("teacherFromId")
    default Teacher teacherFromId(Long id) {
        if (id == null) {
            return null;
        }

        Teacher teacher = new Teacher();
        teacher.setId(id);
        return teacher;
    }

    @Named("studentFromId")
    default Student studentFromId(Long id) {
        if (id == null) {
            return null;
        }

        Student student = new Student();
        student.setId(id);
        return student;
    }
}

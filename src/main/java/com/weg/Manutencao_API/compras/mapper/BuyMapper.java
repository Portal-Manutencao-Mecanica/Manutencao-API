package com.weg.Manutencao_API.compras.mapper;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.compras.dto.request.BuyDtoRequest;
import com.weg.Manutencao_API.compras.dto.response.BuyDtoResponse;
import com.weg.Manutencao_API.compras.entity.Buy;
import com.weg.Manutencao_API.equipamento.entity.Equipment;
import com.weg.Manutencao_API.professor.entity.Teacher;
import com.weg.Manutencao_API.turma.entity.ClassGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "studentId")
    @Mapping(target = "teacher", source = "teacherId")
    @Mapping(target = "classGroup", source = "classGroupId")
    @Mapping(target = "equipment", source = "equipamentId")
    @Mapping(target = "mediaFiles", source = "media")
    @Mapping(target = "createdAt", expression = "java(LocalDate.now())")
    Buy toEntity(BuyDtoRequest request);

    BuyDtoResponse toResponse(Buy buy);

    default Student mapStudent(Long id) {
        if (id == null) {
            return null;
        }

        Student student = new Student();
        student.setId(id);
        return student;
    }

    default Teacher mapTeacher(Long id) {
        if (id == null) {
            return null;
        }

        Teacher teacher = new Teacher();
        teacher.setId(id);
        return teacher;
    }

    default ClassGroup mapClassGroup(Long id) {
        if (id == null) {
            return null;
        }

        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(id);
        return classGroup;
    }

    default Equipment mapEquipment(Long id) {
        if (id == null) {
            return null;
        }

        Equipment equipment = new Equipment();
        equipment.setId(id);
        return equipment;
    }
}

package com.weg.Manutencao_API.compras.dto.request;

import com.weg.Manutencao_API.aluno.entity.Student;
import com.weg.Manutencao_API.equipamento.entity.Equipment;
import com.weg.Manutencao_API.professor.entity.Teacher;
import com.weg.Manutencao_API.turma.entity.ClassGroup;
import com.weg.Manutencao_API.validation.EntityExists;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BuyDtoRequest(

        @NotNull(message = "status can't be null")
        String status,
        @NotNull(message = "student can't be null")
        @EntityExists(entityClass = Student.class, message = "student not found")
        Long studentId,
        @NotNull(message = "teacher can't be null")
        @EntityExists(entityClass = Teacher.class, message = "teacher not found")
        Long teacherId,
        String technicalSpecification,
        @NotNull(message = "quantity can't be null")
        int quantity,
        String sap,
        @NotNull(message = "purchaseJustification can't be null")
        String purchaseJustification,
        @NotNull(message = "classGroup can't be null")
        @EntityExists(entityClass = ClassGroup.class, message = "class group not found")
        Long classGroupId,
        String tag,
        @NotNull(message = "patrimony can't be null")
        String patrimony,
        @NotNull(message = "equipament can't be null")
        @EntityExists(entityClass = Equipment.class, message = "equipment not found")
        Long equipamentId,
        @NotNull(message = "mechanicalSet can't be null")
        String mechanicalSet,
        List<String> media
        ) {
}

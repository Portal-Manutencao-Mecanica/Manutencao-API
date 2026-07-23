package com.weg.Maintenance_API.excel.service;

import com.weg.Maintenance_API.coordinator.entity.Coordinator;
import com.weg.Maintenance_API.coordinator.repository.CoordinatorRepository;
import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.student.entity.Student;
import com.weg.Maintenance_API.student.repository.StudentRepository;
import com.weg.Maintenance_API.teacher.entity.Teacher;
import com.weg.Maintenance_API.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserImportService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CoordinatorRepository coordinatorRepository;
    private final PasswordEncoder passwordEncoder;

    public void importUsers(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo Excel não pode estar vazio.");
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("O arquivo Excel não possui planilhas.");
            }

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String name = cellValue(row, 0, formatter);
                String email = cellValue(row, 1, formatter);
                String roleValue = cellValue(row, 2, formatter);

                if (name.isBlank() && email.isBlank() && roleValue.isBlank()) {
                    continue;
                }
                if (name.isBlank() || email.isBlank() || roleValue.isBlank()) {
                    throw new IllegalArgumentException("Linha " + (i + 1)
                            + " inválida: informe nome, e-mail e perfil.");
                }

                Role role = parseRole(roleValue, i + 1);
                switch (role) {
                    case ALUNO -> studentRepository.save(createStudent(name, email));
                    case PROFESSOR -> teacherRepository.save(createTeacher(name, email));
                    case COORDENADOR -> coordinatorRepository.save(createCoordinator(name, email));
                    default -> throw new IllegalArgumentException("Perfil não permitido na linha " + (i + 1));
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível importar o arquivo Excel.", e);
        }
    }

    private String cellValue(Row row, int column, DataFormatter formatter) {
        if (row.getCell(column) == null) {
            return "";
        }
        return formatter.formatCellValue(row.getCell(column)).trim();
    }

    private Role parseRole(String value, int rowNumber) {
        return switch (value.trim().toUpperCase()) {
            case "ALUNO", "STUDENT" -> Role.ALUNO;
            case "PROFESSOR", "TEACHER" -> Role.PROFESSOR;
            case "COORDENADOR", "COORDINATOR" -> Role.COORDENADOR;
            default -> throw new IllegalArgumentException("Perfil inválido na linha " + rowNumber + ": " + value);
        };
    }

    private Student createStudent(String name, String email) {
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode("123456"));
        student.setRole(Role.ALUNO);
        return student;
    }

    private Teacher createTeacher(String name, String email) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setPassword(passwordEncoder.encode("123456"));
        teacher.setRole(Role.PROFESSOR);
        return teacher;
    }

    private Coordinator createCoordinator(String name, String email) {
        Coordinator coordinator = new Coordinator();
        coordinator.setName(name);
        coordinator.setEmail(email);
        coordinator.setPassword(passwordEncoder.encode("123456"));
        coordinator.setRole(Role.COORDENADOR);
        return coordinator;
    }
}
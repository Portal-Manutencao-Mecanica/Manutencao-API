@Service
@RequiredArgsConstructor
public class UserImportService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final CoordinatorRepository coordinatorRepository;
    private final PasswordEncoder passwordEncoder;

    public void importUsers(MultipartFile file) {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null)
                    continue;

                String name = row.getCell(0).getStringCellValue().trim();
                String email = row.getCell(1).getStringCellValue().trim();
                Role role = Role.valueOf(
                        row.getCell(2).getStringCellValue().trim().toUpperCase());

                switch (role) {

                    case STUDENT -> studentRepository.save(createStudent(name, email));

                    case PROFESSOR -> professorRepository.save(createProfessor(name, email));

                    case COORDINATOR -> coordinatorRepository.save(createCoordinator(name, email));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Student createStudent(String name, String email) {

        Student student = new Student();

        student.setName(name);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode("123456"));
        student.setRole(Role.STUDENT);

        return student;
    }

    private Professor createProfessor(String name, String email) {

        Professor professor = new Professor();

        professor.setName(name);
        professor.setEmail(email);
        professor.setPassword(passwordEncoder.encode("123456"));
        professor.setRole(Role.PROFESSOR);

        return professor;
    }

    private Coordinator createCoordinator(String name, String email) {

        Coordinator coordinator = new Coordinator();

        coordinator.setName(name);
        coordinator.setEmail(email);
        coordinator.setPassword(passwordEncoder.encode("123456"));
        coordinator.setRole(Role.COORDINATOR);

        return coordinator;
    }
}
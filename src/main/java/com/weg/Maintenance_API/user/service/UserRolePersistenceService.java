package com.weg.Maintenance_API.user.service;

import com.weg.Maintenance_API.enums.Role;
import com.weg.Maintenance_API.exception.type.ConflictException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRolePersistenceService {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    // Executa a operacao deste metodo.
    public void transition(UUID userId, Role currentRole, Role targetRole) {
        entityManager.flush();
        try {
            int deleted = jdbcTemplate.update(
                    "delete from " + tableFor(currentRole) + " where user_id = ?",
                    userId
            );
            if (deleted != 1) {
                throw new ConflictException(
                        "A role atual do usuÃ¡rio estÃ¡ inconsistente. A alteraÃ§Ã£o nÃ£o foi realizada."
                );
            }
            jdbcTemplate.update(
                    "insert into " + tableFor(targetRole) + " (user_id) values (?)",
                    userId
            );
            int updated = jdbcTemplate.update(
                    """
                    update users
                       set user_role = ?,
                           updated_at = current_timestamp,
                           security_version = security_version + 1,
                           version = version + 1
                     where user_id = ?
                    """,
                    targetRole.name(),
                    userId
            );
            if (updated != 1) {
                throw new ConflictException(
                        "O usuÃ¡rio nÃ£o foi encontrado durante a alteraÃ§Ã£o de role."
                );
            }
            entityManager.clear();
        } catch (DataAccessException exception) {
            throw new ConflictException(
                    "A role nÃ£o pode ser alterada porque o usuÃ¡rio possui vÃ­nculos incompatÃ­veis."
            );
        }
    }

    // Executa a operacao deste metodo.
    private String tableFor(Role role) {
        return switch (role) {
            case ADMIN -> "admin";
            case COORDENADOR -> "coordinator";
            case PROFESSOR -> "teacher";
            case ALUNO -> "student";
        };
    }
}

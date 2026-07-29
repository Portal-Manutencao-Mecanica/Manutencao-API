package com.weg.Maintenance_API;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlMigrationIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("postgres:17-alpine")
    );

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.placeholders.seed_test_users", () -> "true");
        registry.add("server.servlet.context-path", () -> "/api");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    @Test
    void applicationStartsMigrationsRunAndEntityPrimaryKeysRemainUuid()
            throws Exception {
        HttpResponse<String> health = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:" + port + "/api/actuator/health"
                        ))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, health.statusCode());
        assertTrue(health.body().contains("\"status\":\"UP\""));

        Integer migrationCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                  from flyway_schema_history
                 where success = true
                """,
                Integer.class
        );
        assertTrue(migrationCount != null && migrationCount >= 3);

        Integer testUserMigration = jdbcTemplate.queryForObject(
                """
                select count(*)
                  from flyway_schema_history
                 where version = '3'
                   and success = true
                """,
                Integer.class
        );
        assertEquals(1, testUserMigration);

        List<Map<String, Object>> testUsers = jdbcTemplate.queryForList(
                """
                select user_email, user_role, user_password
                  from public.users
                 where user_email in (
                    'admin@teste.local',
                    'coordenador@teste.local',
                    'professor@teste.local',
                    'aluno@teste.local'
                 )
                 order by user_email
                """
        );
        assertEquals(4, testUsers.size());
        assertTrue(testUsers.stream().anyMatch(user ->
                "admin@teste.local".equals(user.get("user_email"))
                        && "ADMIN".equals(user.get("user_role"))));
        assertTrue(testUsers.stream().anyMatch(user ->
                "coordenador@teste.local".equals(user.get("user_email"))
                        && "COORDENADOR".equals(user.get("user_role"))));
        assertTrue(testUsers.stream().anyMatch(user ->
                "professor@teste.local".equals(user.get("user_email"))
                        && "PROFESSOR".equals(user.get("user_role"))));
        assertTrue(testUsers.stream().anyMatch(user ->
                "aluno@teste.local".equals(user.get("user_email"))
                        && "ALUNO".equals(user.get("user_role"))));
        assertTrue(testUsers.stream().allMatch(user -> passwordEncoder.matches(
                "Senha@123",
                (String) user.get("user_password")
        )));

        List<Map<String, Object>> numericPrimaryKeys = jdbcTemplate.queryForList(
                """
                select columns.table_name,
                       columns.column_name,
                       columns.data_type
                  from information_schema.table_constraints constraints
                  join information_schema.key_column_usage keys
                    on keys.constraint_name = constraints.constraint_name
                   and keys.constraint_schema = constraints.constraint_schema
                  join information_schema.columns columns
                    on columns.table_schema = keys.table_schema
                   and columns.table_name = keys.table_name
                   and columns.column_name = keys.column_name
                 where constraints.constraint_type = 'PRIMARY KEY'
                   and constraints.table_schema = 'public'
                   and constraints.table_name <> 'flyway_schema_history'
                   and columns.data_type <> 'uuid'
                """
        );
        assertTrue(
                numericPrimaryKeys.isEmpty(),
                "Chaves primárias não UUID encontradas: " + numericPrimaryKeys
        );

        String statusActorType = jdbcTemplate.queryForObject(
                """
                select data_type
                  from information_schema.columns
                 where table_schema = 'public'
                   and table_name = 'users'
                   and column_name = 'status_changed_by'
                """,
                String.class
        );
        assertEquals("uuid", statusActorType);
    }
}

package com.weg.Maintenance_API.userimport.service;

import com.weg.Maintenance_API.audit.service.AuditService;
import com.weg.Maintenance_API.auth.service.ClientRequestMetadata;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import com.weg.Maintenance_API.user.UserRepository;
import com.weg.Maintenance_API.user.entity.User;
import com.weg.Maintenance_API.user.service.UserIdentityPolicy;
import com.weg.Maintenance_API.userimport.dto.UserImportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserImportService {

    private final SpreadsheetUserReader spreadsheetUserReader;
    private final UserImportLifecycleService lifecycleService;
    private final UserImportRowProcessor rowProcessor;
    private final UserRepository userRepository;
    private final UserIdentityPolicy identityPolicy;
    private final AuditService auditService;

    public UserImportResponse importUsers(
            MultipartFile file,
            String actorEmail,
            ClientRequestMetadata metadata
    ) {
        User actor = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado"));
        SpreadsheetUserReader.SpreadsheetData spreadsheet;
        try {
            spreadsheet = spreadsheetUserReader.read(file);
        } catch (RuntimeException exception) {
            auditService.record(
                    actor,
                    "USER_IMPORT_REJECTED",
                    "USER_IMPORT",
                    null,
                    metadata.endpoint(),
                    metadata.httpMethod(),
                    metadata.ipAddress(),
                    metadata.userAgent(),
                    false,
                    "Arquivo de importação rejeitado."
            );
            throw exception;
        }

        UUID importId = lifecycleService.start(
                spreadsheet.filename(),
                actor.getId(),
                spreadsheet.rows().size()
        );
        Map<String, Integer> emailCounts = counts(
                spreadsheet.rows(),
                row -> identityPolicy.normalizeEmail(row.email())
        );
        Map<String, Integer> usernameCounts = counts(
                spreadsheet.rows(),
                row -> identityPolicy.normalizeUsername(row.username())
        );

        int created = 0;
        int failed = 0;
        for (SpreadsheetUserRow row : spreadsheet.rows()) {
            try {
                rowProcessor.process(
                        importId,
                        actor.getId(),
                        row,
                        duplicate(emailCounts, identityPolicy.normalizeEmail(row.email())),
                        duplicate(usernameCounts, identityPolicy.normalizeUsername(row.username())),
                        metadata
                );
                created++;
            } catch (UserImportRowException exception) {
                failed++;
                rowProcessor.recordFailure(importId, row, exception);
            } catch (DataIntegrityViolationException exception) {
                failed++;
                rowProcessor.recordFailure(
                        importId,
                        row,
                        new UserImportRowException(
                                "DATA_CONFLICT",
                                "email",
                                "O e-mail ou username já foi cadastrado por outra operação.",
                                null
                        )
                );
            } catch (RuntimeException exception) {
                failed++;
                rowProcessor.recordFailure(
                        importId,
                        row,
                        new UserImportRowException(
                                "ROW_PROCESSING_ERROR",
                                "row",
                                "A linha não pôde ser processada.",
                                null
                        )
                );
            }
        }

        lifecycleService.complete(importId, created, failed);
        auditService.record(
                actor,
                "USER_IMPORT_COMPLETED",
                "USER_IMPORT",
                importId,
                metadata.endpoint(),
                metadata.httpMethod(),
                metadata.ipAddress(),
                metadata.userAgent(),
                true,
                "Registros: " + spreadsheet.rows().size()
                        + "; criados: " + created
                        + "; falhas: " + failed
        );
        return lifecycleService.response(importId);
    }

    private Map<String, Integer> counts(
            List<SpreadsheetUserRow> rows,
            Function<SpreadsheetUserRow, String> extractor
    ) {
        Map<String, Integer> counts = new HashMap<>();
        for (SpreadsheetUserRow row : rows) {
            String value = extractor.apply(row);
            if (!value.isBlank()) {
                counts.merge(value, 1, Integer::sum);
            }
        }
        return counts;
    }

    private boolean duplicate(Map<String, Integer> counts, String value) {
        return !value.isBlank() && counts.getOrDefault(value, 0) > 1;
    }
}

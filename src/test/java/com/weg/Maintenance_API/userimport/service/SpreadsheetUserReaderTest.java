package com.weg.Maintenance_API.userimport.service;

import com.weg.Maintenance_API.exception.type.InvalidSpreadsheetException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpreadsheetUserReaderTest {

    private static final String XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final SpreadsheetUserReader reader =
            new SpreadsheetUserReader(1024 * 1024, 10);

    @Test
    void readsRequiredColumnsAndOptionalOrganization() throws Exception {
        MockMultipartFile file = workbook(
                new String[]{"name", "username", "email", "role", "organization"},
                new String[]{"Aluno", "aluno.teste", "aluno@local.com", "ALUNO", "Local"}
        );

        SpreadsheetUserReader.SpreadsheetData data = reader.read(file);

        assertEquals(1, data.rows().size());
        assertEquals("aluno.teste", data.rows().getFirst().username());
        assertEquals("Local", data.rows().getFirst().organization());
    }

    @Test
    void rejectsMissingHeader() throws Exception {
        MockMultipartFile file = workbook(
                new String[]{"name", "email", "role"},
                new String[]{"Aluno", "aluno@local.com", "ALUNO"}
        );

        assertThrows(InvalidSpreadsheetException.class, () -> reader.read(file));
    }

    @Test
    void rejectsWrongExtensionAndMimeType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.xls",
                "application/vnd.ms-excel",
                new byte[]{1, 2, 3}
        );

        assertThrows(InvalidSpreadsheetException.class, () -> reader.read(file));
    }

    private MockMultipartFile workbook(
            String[] headers,
            String[] values
    ) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("users");
            var header = sheet.createRow(0);
            for (int index = 0; index < headers.length; index++) {
                header.createCell(index).setCellValue(headers[index]);
            }
            var row = sheet.createRow(1);
            for (int index = 0; index < values.length; index++) {
                row.createCell(index).setCellValue(values[index]);
            }
            workbook.write(output);
            return new MockMultipartFile(
                    "file",
                    "users.xlsx",
                    XLSX,
                    output.toByteArray()
            );
        }
    }
}

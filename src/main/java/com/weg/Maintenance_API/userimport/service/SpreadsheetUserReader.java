package com.weg.Maintenance_API.userimport.service;

import com.weg.Maintenance_API.exception.type.InvalidSpreadsheetException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class SpreadsheetUserReader {

    private static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final Set<String> REQUIRED_HEADERS =
            Set.of("name", "username", "email", "role");

    private final long maxFileSizeBytes;
    private final int maxRows;

    public SpreadsheetUserReader(
            @Value("${app.user-import.max-file-size-bytes:5242880}") long maxFileSizeBytes,
            @Value("${app.user-import.max-rows:1000}") int maxRows
    ) {
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.maxRows = maxRows;
    }

    public SpreadsheetData read(MultipartFile file) {
        validateFile(file);
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new InvalidSpreadsheetException("A planilha não possui abas.");
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0 || sheet.getRow(0) == null) {
                throw new InvalidSpreadsheetException("A planilha não possui cabeçalho.");
            }
            if (sheet.getLastRowNum() > maxRows) {
                throw new InvalidSpreadsheetException(
                        "A planilha excede o limite de " + maxRows + " registros."
                );
            }

            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            Map<String, Integer> columns = readHeaders(sheet.getRow(0), formatter);
            List<SpreadsheetUserRow> rows = new ArrayList<>();
            for (int index = 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                rows.add(readRow(row, index + 1, columns, formatter));
            }
            if (rows.isEmpty()) {
                throw new InvalidSpreadsheetException("A planilha não possui registros para importar.");
            }
            return new SpreadsheetData(safeFilename(file.getOriginalFilename()), List.copyOf(rows));
        } catch (InvalidSpreadsheetException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            throw new InvalidSpreadsheetException(
                    "O arquivo não é uma planilha XLSX válida."
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidSpreadsheetException("O arquivo XLSX é obrigatório.");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new InvalidSpreadsheetException(
                    "O arquivo excede o tamanho máximo permitido."
            );
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new InvalidSpreadsheetException("Somente arquivos com extensão .xlsx são permitidos.");
        }
        if (!XLSX_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
            throw new InvalidSpreadsheetException("O MIME type do arquivo XLSX é inválido.");
        }
    }

    private Map<String, Integer> readHeaders(Row header, DataFormatter formatter) {
        Map<String, Integer> columns = new HashMap<>();
        for (Cell cell : header) {
            rejectFormula(cell);
            String value = formatter.formatCellValue(cell).trim().toLowerCase(Locale.ROOT);
            if (!value.isBlank()) {
                if (columns.putIfAbsent(value, cell.getColumnIndex()) != null) {
                    throw new InvalidSpreadsheetException(
                            "O cabeçalho possui a coluna duplicada: " + value + "."
                    );
                }
            }
        }
        if (!columns.keySet().containsAll(REQUIRED_HEADERS)) {
            throw new InvalidSpreadsheetException(
                    "Cabeçalhos obrigatórios: name, username, email e role."
            );
        }
        return columns;
    }

    private SpreadsheetUserRow readRow(
            Row row,
            int rowNumber,
            Map<String, Integer> columns,
            DataFormatter formatter
    ) {
        return new SpreadsheetUserRow(
                rowNumber,
                value(row, columns.get("name"), formatter),
                value(row, columns.get("username"), formatter),
                value(row, columns.get("email"), formatter),
                value(row, columns.get("role"), formatter),
                value(row, columns.get("organization"), formatter)
        );
    }

    private String value(Row row, Integer column, DataFormatter formatter) {
        if (row == null || column == null) {
            return "";
        }
        Cell cell = row.getCell(column);
        if (cell == null) {
            return "";
        }
        rejectFormula(cell);
        return formatter.formatCellValue(cell).trim();
    }

    private void rejectFormula(Cell cell) {
        if (cell.getCellType() == CellType.FORMULA) {
            throw new InvalidSpreadsheetException(
                    "Fórmulas não são permitidas na planilha de importação."
            );
        }
    }

    private String safeFilename(String originalFilename) {
        return Path.of(originalFilename).getFileName().toString();
    }

    public record SpreadsheetData(
            String filename,
            List<SpreadsheetUserRow> rows
    ) {
    }
}

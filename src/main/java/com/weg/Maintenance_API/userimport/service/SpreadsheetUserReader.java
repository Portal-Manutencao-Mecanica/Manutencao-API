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
import java.nio.charset.StandardCharsets;
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
    private static final Set<String> CSV_CONTENT_TYPES = Set.of(
            "text/csv",
            "application/csv",
            "application/vnd.ms-excel",
            "application/octet-stream"
    );
    private static final Set<String> REQUIRED_HEADERS =
            Set.of("name", "email", "role", "organization", "classgroupids");

    private final long maxFileSizeBytes;
    private final int maxRows;

    public SpreadsheetUserReader(
            @Value("${app.user-import.max-file-size-bytes:5242880}") long maxFileSizeBytes,
            @Value("${app.user-import.max-rows:1000}") int maxRows
    ) {
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.maxRows = maxRows;
    }

    // Lê um arquivo CSV ou XLSX com os dados necessários para importar usuários.
    public SpreadsheetData read(MultipartFile file) {
        FileFormat format = validateFile(file);
        return format == FileFormat.CSV ? readCsv(file) : readXlsx(file);
    }

    private SpreadsheetData readXlsx(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new InvalidSpreadsheetException("A planilha não possui abas.");
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0 || sheet.getRow(0) == null) {
                throw new InvalidSpreadsheetException("A planilha não possui cabeçalho.");
            }
            if (sheet.getLastRowNum() > maxRows) {
                throw rowLimitExceeded();
            }

            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            Map<String, Integer> columns = readHeaders(sheet.getRow(0), formatter);
            List<SpreadsheetUserRow> rows = new ArrayList<>();
            for (int index = 1; index <= sheet.getLastRowNum(); index++) {
                rows.add(readRow(sheet.getRow(index), index + 1, columns, formatter));
            }
            return data(file, rows);
        } catch (InvalidSpreadsheetException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            throw new InvalidSpreadsheetException("O arquivo não é uma planilha XLSX válida.");
        }
    }

    private SpreadsheetData readCsv(MultipartFile file) {
        try {
            List<List<String>> records = parseCsv(
                    new String(file.getBytes(), StandardCharsets.UTF_8)
            );
            if (records.isEmpty()) {
                throw new InvalidSpreadsheetException("O CSV não possui cabeçalho.");
            }
            if (records.size() - 1 > maxRows) {
                throw rowLimitExceeded();
            }

            Map<String, Integer> columns = readHeaders(records.getFirst());
            List<SpreadsheetUserRow> rows = new ArrayList<>();
            for (int index = 1; index < records.size(); index++) {
                rows.add(readRow(records.get(index), index + 1, columns));
            }
            return data(file, rows);
        } catch (InvalidSpreadsheetException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new InvalidSpreadsheetException("Não foi possível ler o arquivo CSV.");
        }
    }

    private SpreadsheetData data(MultipartFile file, List<SpreadsheetUserRow> rows) {
        if (rows.isEmpty()) {
            throw new InvalidSpreadsheetException("O arquivo não possui registros para importar.");
        }
        return new SpreadsheetData(safeFilename(file.getOriginalFilename()), List.copyOf(rows));
    }

    private FileFormat validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidSpreadsheetException("O arquivo CSV ou XLSX é obrigatório.");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new InvalidSpreadsheetException("O arquivo excede o tamanho máximo permitido.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new InvalidSpreadsheetException("O nome do arquivo é obrigatório.");
        }
        String normalizedFilename = filename.toLowerCase(Locale.ROOT);
        if (normalizedFilename.endsWith(".csv")) {
            String contentType = file.getContentType();
            if (contentType != null && !CSV_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
                throw new InvalidSpreadsheetException("O MIME type do arquivo CSV é inválido.");
            }
            return FileFormat.CSV;
        }
        if (normalizedFilename.endsWith(".xlsx")
                && XLSX_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
            return FileFormat.XLSX;
        }
        throw new InvalidSpreadsheetException("Somente arquivos CSV ou XLSX são permitidos.");
    }

    private Map<String, Integer> readHeaders(Row header, DataFormatter formatter) {
        Map<String, Integer> columns = new HashMap<>();
        for (Cell cell : header) {
            rejectFormula(cell);
            addHeader(columns, formatter.formatCellValue(cell), cell.getColumnIndex());
        }
        validateRequiredHeaders(columns);
        return columns;
    }

    private Map<String, Integer> readHeaders(List<String> header) {
        Map<String, Integer> columns = new HashMap<>();
        for (int index = 0; index < header.size(); index++) {
            addHeader(columns, header.get(index), index);
        }
        validateRequiredHeaders(columns);
        return columns;
    }

    private void addHeader(Map<String, Integer> columns, String value, int index) {
        String normalized = value.replaceFirst("^\\uFEFF", "").trim().toLowerCase(Locale.ROOT);
        if (!normalized.isBlank() && columns.putIfAbsent(normalized, index) != null) {
            throw new InvalidSpreadsheetException("O cabeçalho possui a coluna duplicada: " + normalized + ".");
        }
    }

    private void validateRequiredHeaders(Map<String, Integer> columns) {
        if (!columns.keySet().containsAll(REQUIRED_HEADERS)) {
            throw new InvalidSpreadsheetException(
                    "Cabeçalhos obrigatórios: name, email, role, organization e classGroupIds."
            );
        }
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
                value(row, columns.get("email"), formatter),
                value(row, columns.get("role"), formatter),
                value(row, columns.get("organization"), formatter),
                value(row, columns.get("classgroupids"), formatter)
        );
    }

    private SpreadsheetUserRow readRow(
            List<String> row,
            int rowNumber,
            Map<String, Integer> columns
    ) {
        return new SpreadsheetUserRow(
                rowNumber,
                value(row, columns.get("name")),
                value(row, columns.get("email")),
                value(row, columns.get("role")),
                value(row, columns.get("organization")),
                value(row, columns.get("classgroupids"))
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

    private String value(List<String> row, Integer column) {
        if (column == null || column >= row.size()) {
            return "";
        }
        return row.get(column).trim();
    }

    private List<List<String>> parseCsv(String content) {
        List<List<String>> records = new ArrayList<>();
        List<String> record = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        char delimiter = detectDelimiter(content);

        for (int index = 0; index < content.length(); index++) {
            char character = content.charAt(index);
            if (quoted) {
                if (character == '"') {
                    if (index + 1 < content.length() && content.charAt(index + 1) == '"') {
                        value.append('"');
                        index++;
                    } else {
                        quoted = false;
                    }
                } else {
                    value.append(character);
                }
                continue;
            }

            if (character == '"') {
                if (value.length() != 0) {
                    throw new InvalidSpreadsheetException("O CSV possui aspas em posição inválida.");
                }
                quoted = true;
            } else if (character == delimiter) {
                record.add(value.toString());
                value.setLength(0);
            } else if (character == '\n' || character == '\r') {
                if (character == '\r' && index + 1 < content.length()
                        && content.charAt(index + 1) == '\n') {
                    index++;
                }
                record.add(value.toString());
                records.add(record);
                record = new ArrayList<>();
                value.setLength(0);
            } else {
                value.append(character);
            }
        }

        if (quoted) {
            throw new InvalidSpreadsheetException("O CSV possui aspas não finalizadas.");
        }
        if (!record.isEmpty() || value.length() > 0) {
            record.add(value.toString());
            records.add(record);
        }
        return records;
    }

    private char detectDelimiter(String content) {
        int commas = 0;
        int semicolons = 0;
        int tabs = 0;
        boolean quoted = false;

        for (int index = 0; index < content.length(); index++) {
            char character = content.charAt(index);
            if (character == '"') {
                if (quoted && index + 1 < content.length() && content.charAt(index + 1) == '"') {
                    index++;
                } else {
                    quoted = !quoted;
                }
                continue;
            }
            if (!quoted && (character == '\n' || character == '\r')) {
                break;
            }
            if (!quoted && character == ',') {
                commas++;
            } else if (!quoted && character == ';') {
                semicolons++;
            } else if (!quoted && character == '\t') {
                tabs++;
            }
        }

        if (tabs > commas && tabs > semicolons) {
            return '\t';
        }
        if (semicolons > commas) {
            return ';';
        }
        return ',';
    }

    private InvalidSpreadsheetException rowLimitExceeded() {
        return new InvalidSpreadsheetException(
                "O arquivo excede o limite de " + maxRows + " registros."
        );
    }

    private void rejectFormula(Cell cell) {
        if (cell.getCellType() == CellType.FORMULA) {
            throw new InvalidSpreadsheetException("Fórmulas não são permitidas na planilha de importação.");
        }
    }

    private String safeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "importacao";
        }
        return Path.of(originalFilename).getFileName().toString();
    }

    private enum FileFormat {
        CSV,
        XLSX
    }

    public record SpreadsheetData(
            String filename,
            List<SpreadsheetUserRow> rows
    ) {
    }
}

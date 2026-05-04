package ocsms.pattern.strategy;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * STRATEGY PATTERN — ExcelExportStrategy (Concrete Strategy)
 * Exports data as a CSV file (opens in Excel / LibreOffice Calc).
 * No external library required.
 * Used in: UC-09, UC-16
 */
public class ExcelExportStrategy implements ExportStrategy {

    @Override
    public void export(String[] headers, List<String[]> rows, String title, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // ── Metadata rows ─────────────────────────────────────────────────
            writer.println("OCSMS Export," + escapeCSV(title));
            writer.println("Generated," + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a")));
            writer.println(); // blank row

            // ── Column headers ────────────────────────────────────────────────
            StringBuilder headerLine = new StringBuilder();
            for (int i = 0; i < headers.length; i++) {
                if (i > 0) headerLine.append(",");
                headerLine.append(escapeCSV(headers[i]));
            }
            writer.println(headerLine);

            // ── Data rows ─────────────────────────────────────────────────────
            for (String[] row : rows) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < headers.length; i++) {
                    if (i > 0) line.append(",");
                    String cell = (i < row.length && row[i] != null) ? row[i] : "";
                    line.append(escapeCSV(cell));
                }
                writer.println(line);
            }

            // ── Footer ────────────────────────────────────────────────────────
            writer.println();
            writer.println("Total Records," + rows.size());
        }
    }

    /** Wraps cell in quotes and escapes internal quotes for CSV safety */
    private String escapeCSV(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @Override
    public String getFileExtension() { return "csv"; }

    @Override
    public String getFormatName() { return "Excel (CSV)"; }
}

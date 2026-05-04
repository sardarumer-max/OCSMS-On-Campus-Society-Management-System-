package ocsms.pattern.strategy;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * STRATEGY PATTERN — PDFExportStrategy (Concrete Strategy)
 * Exports data as a plain-text formatted report (simulating PDF without iText dependency).
 * File is saved as .txt with PDF-like formatting for portability.
 * Used in: UC-09, UC-16
 */
public class PDFExportStrategy implements ExportStrategy {

    @Override
    public void export(String[] headers, List<String[]> rows, String title, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // ── Header Section ────────────────────────────────────────────────
            writer.println("=".repeat(70));
            writer.println("  OCSMS — On-Campus Societies Management System");
            writer.println("  " + title);
            writer.println("  Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
            writer.println("=".repeat(70));
            writer.println();

            // ── Column Headers ────────────────────────────────────────────────
            int[] widths = calculateColumnWidths(headers, rows);
            String separator = buildSeparator(widths);

            writer.println(separator);
            writer.print("| ");
            for (int i = 0; i < headers.length; i++) {
                writer.printf("%-" + widths[i] + "s | ", headers[i]);
            }
            writer.println();
            writer.println(separator);

            // ── Data Rows ─────────────────────────────────────────────────────
            for (String[] row : rows) {
                writer.print("| ");
                for (int i = 0; i < headers.length; i++) {
                    String cell = (i < row.length && row[i] != null) ? row[i] : "";
                    writer.printf("%-" + widths[i] + "s | ", cell);
                }
                writer.println();
            }
            writer.println(separator);

            // ── Footer ────────────────────────────────────────────────────────
            writer.println();
            writer.println("Total records: " + rows.size());
            writer.println();
            writer.println("FAST-NUCES Peshawar — Confidential Document");
            writer.println("=".repeat(70));
        }
    }

    private int[] calculateColumnWidths(String[] headers, List<String[]> rows) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
        for (String[] row : rows) {
            for (int i = 0; i < headers.length && i < row.length; i++) {
                if (row[i] != null) widths[i] = Math.max(widths[i], row[i].length());
            }
        }
        return widths;
    }

    private String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) sb.append("-".repeat(w + 2)).append("+");
        return sb.toString();
    }

    @Override
    public String getFileExtension() { return "txt"; }

    @Override
    public String getFormatName() { return "PDF Report (Text)"; }
}

package ocsms.service;

import ocsms.pattern.strategy.ExportStrategy;

import java.util.List;

/**
 * STRATEGY PATTERN — ExportService (Context)
 * Delegates export work to whichever ExportStrategy is passed in at runtime.
 * The UI selects the strategy from a JComboBox; this class is strategy-agnostic.
 * Used in: UC-09 (Finance), UC-16 (Dashboard)
 */
public class ExportService {

    /**
     * Executes the export using the provided strategy.
     * Caller selects the concrete strategy (PDF or Excel) at runtime.
     *
     * @param strategy  The export format strategy (injected at runtime)
     * @param headers   Column headers
     * @param rows      Table data rows
     * @param title     Document title
     * @param filePath  Destination path (from JFileChooser)
     */
    public static void export(ExportStrategy strategy, String[] headers,
                               List<String[]> rows, String title, String filePath) throws Exception {
        // Delegate entirely to the chosen strategy — no if/else needed
        strategy.export(headers, rows, title, filePath);
    }
}

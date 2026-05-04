package ocsms.pattern.strategy;

import java.util.List;

/**
 * STRATEGY PATTERN — ExportStrategy (Strategy Interface)
 * Defines the contract for exporting data in different formats (PDF, Excel).
 * Prevents if/else blocks in the UI — correct strategy is injected at runtime.
 * Used in: UC-09 (Finance), UC-16 (Dashboard)
 */
public interface ExportStrategy {

    /**
     * Export the given rows of data to the specified file path.
     * @param headers   Column header labels
     * @param rows      Table data rows (each row is a String array)
     * @param title     Title for the exported document
     * @param filePath  Destination file path chosen by user via JFileChooser
     */
    void export(String[] headers, List<String[]> rows, String title, String filePath) throws Exception;

    /** Returns the file extension used by this strategy (e.g. "pdf", "csv") */
    String getFileExtension();

    /** Human-readable format name shown in JComboBox */
    String getFormatName();
}

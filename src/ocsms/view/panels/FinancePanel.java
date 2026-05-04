package ocsms.view.panels;

import ocsms.controller.FinanceController;
import ocsms.model.FinanceEntry;
import ocsms.model.FinanceEntry.EntryCategory;
import ocsms.model.Society;
import ocsms.model.User.UserRole;
import ocsms.pattern.strategy.ExcelExportStrategy;
import ocsms.pattern.strategy.ExportStrategy;
import ocsms.pattern.strategy.PDFExportStrategy;
import ocsms.service.ExportService;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * VIEW (MVC) — FinancePanel
 * UC-09: Add income/expense entries, view running balance, export via Strategy pattern.
 * Balance label turns red when negative.
 */
public class FinancePanel extends JPanel {

    private final FinanceController ctrl = new FinanceController();
    private final UserRole role = SessionManager.getInstance().getCurrentUser().getRole();

    private JLabel balanceLabel;
    private DefaultTableModel tableModel;
    private Society selectedSociety;

    public FinancePanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Finance Tracker"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Society selector (for Admin/Advisor)
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterBar.setBackground(UITheme.BG);
        filterBar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JComboBox<Society> societyCombo = new JComboBox<>();
        UITheme.styleCombo(societyCombo);
        DataStore.getInstance().getSocieties().forEach(societyCombo::addItem);
        selectedSociety = DataStore.getInstance().getSocieties().isEmpty()
                ? null : DataStore.getInstance().getSocieties().get(0);

        societyCombo.addActionListener(e -> {
            selectedSociety = (Society) societyCombo.getSelectedItem();
            refreshTable();
            refreshBalance();
        });

        filterBar.add(UITheme.label("Society:"));
        filterBar.add(societyCombo);

        // Running balance label
        balanceLabel = new JLabel("Balance: Rs. 0.00");
        balanceLabel.setFont(UITheme.FONT_BOLD);
        balanceLabel.setForeground(UITheme.SUCCESS);
        filterBar.add(Box.createHorizontalStrut(30));
        filterBar.add(balanceLabel);

        // filterBar is added to center panel's NORTH below

        // ── Main split layout ─────────────────────────────────────────────────
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(UITheme.BG);
        center.setBorder(new EmptyBorder(8, 24, 24, 24));

        center.add(filterBar, BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(16, 0));
        south.setBackground(UITheme.BG);
        if (role == UserRole.TREASURER || role == UserRole.SOCIETY_ADMIN) {
            south.add(buildAddEntryForm(), BorderLayout.CENTER);
        }
        south.add(buildExportPanel(), BorderLayout.EAST);
        center.add(south, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        refreshTable();
        refreshBalance();
    }

    // ── Finance Table ────────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(UITheme.cardBorder());

        String[] cols = {"Date", "Description", "Category", "Amount (Rs.)", "Receipt"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Category colour renderer
        table.getColumnModel().getColumn(2).setCellRenderer(new UITheme.DarkTableRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if ("INCOME".equals(v))   setForeground(UITheme.SUCCESS);
                else if ("EXPENSE".equals(v)) setForeground(UITheme.DANGER);
                return this;
            }
        });

        panel.add(UITheme.scrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (selectedSociety == null) return;
        for (FinanceEntry e : ctrl.getEntriesForSociety(selectedSociety.getId())) {
            tableModel.addRow(new Object[]{
                    DateUtil.format(e.getDate()),
                    e.getDescription(),
                    e.getCategory().name(),
                    String.format("%.2f", e.getAmount()),
                    e.hasReceipt() ? "Yes" : "No Receipt"
            });
        }
    }

    private void refreshBalance() {
        if (selectedSociety == null) return;
        double balance = ctrl.getBalance(selectedSociety.getId());
        balanceLabel.setText(String.format("Balance: Rs. %.2f", balance));
        balanceLabel.setForeground(balance < 0 ? UITheme.DANGER : UITheme.SUCCESS);
    }

    // ── Add Entry Form ───────────────────────────────────────────────────────────
    private JPanel buildAddEntryForm() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.cardBorder());
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridy = 0; card.add(UITheme.heading("Add Entry"), gbc);

        gbc.gridy++; card.add(UITheme.label("Description *"), gbc);
        gbc.gridy++; JTextField descF = UITheme.textField(""); card.add(descF, gbc);

        gbc.gridy++; card.add(UITheme.label("Amount (Rs.) *"), gbc);
        gbc.gridy++; JTextField amtF = UITheme.textField("0.00"); card.add(amtF, gbc);

        gbc.gridy++; card.add(UITheme.label("Category"), gbc);
        gbc.gridy++;
        JComboBox<EntryCategory> catCombo = new JComboBox<>(EntryCategory.values());
        UITheme.styleCombo(catCombo);
        card.add(catCombo, gbc);

        gbc.gridy++; card.add(UITheme.label("Receipt (optional .pdf/.jpg)"), gbc);
        gbc.gridy++;
        JPanel receiptRow = new JPanel(new BorderLayout(6, 0));
        receiptRow.setBackground(UITheme.BG_CARD);
        JTextField receiptF = UITheme.textField("No file");
        receiptF.setEditable(false);
        JButton browseBtn = UITheme.ghostButton("Browse");
        final String[] receiptPath = {null};
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                receiptPath[0] = fc.getSelectedFile().getAbsolutePath();
                receiptF.setText(fc.getSelectedFile().getName());
            }
        });
        receiptRow.add(receiptF, BorderLayout.CENTER);
        receiptRow.add(browseBtn, BorderLayout.EAST);
        card.add(receiptRow, gbc);

        gbc.gridy++;
        JButton addBtn = UITheme.accentButton("Add Entry");
        addBtn.addActionListener(e -> {
            double amt;
            try { amt = Double.parseDouble(amtF.getText().trim()); }
            catch (NumberFormatException ex) { showError("Invalid amount."); return; }

            if (selectedSociety == null) { showError("Please select a society."); return; }

            String err = ctrl.addEntry(selectedSociety.getId(), descF.getText().trim(),
                    amt, LocalDate.now(), (EntryCategory) catCombo.getSelectedItem(),
                    receiptPath[0]);
            if (err != null) showError(err);
            else {
                refreshTable();
                refreshBalance();
                descF.setText(""); amtF.setText("0.00");
                receiptPath[0] = null; receiptF.setText("No file");
                JOptionPane.showMessageDialog(this, "Entry added successfully.",
                        "Added", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card.add(addBtn, gbc);

        return card;
    }

    // ── Export Panel (Strategy Pattern) ─────────────────────────────────────────
    private JPanel buildExportPanel() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.cardBorder());
        card.setPreferredSize(new Dimension(220, 0));
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridy = 0; card.add(UITheme.heading("Export Report"), gbc);

        gbc.gridy++;
        ExportStrategy[] strategies = {new PDFExportStrategy(), new ExcelExportStrategy()};
        JComboBox<ExportStrategy> stratCombo = new JComboBox<>(strategies);
        // Custom renderer to show format name
        stratCombo.setRenderer((list, value, idx, sel, focus) -> {
            JLabel lbl = new JLabel(value != null ? value.getFormatName() : "");
            lbl.setFont(UITheme.FONT_BODY);
            lbl.setForeground(UITheme.TEXT);
            lbl.setBackground(sel ? UITheme.BG_HOVER : UITheme.BG_CARD);
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
            return lbl;
        });
        UITheme.styleCombo(stratCombo);
        card.add(stratCombo, gbc);

        gbc.gridy++;
        JButton exportBtn = UITheme.accentButton("Export");
        exportBtn.setPreferredSize(new Dimension(160, 34));
        exportBtn.addActionListener(e -> handleExport((ExportStrategy) stratCombo.getSelectedItem()));
        card.add(exportBtn, gbc);

        return card;
    }

    private void handleExport(ExportStrategy strategy) {
        if (selectedSociety == null) { showError("Select a society first."); return; }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("finance_report." + strategy.getFileExtension()));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        List<FinanceEntry> entries = ctrl.getEntriesForSociety(selectedSociety.getId());
        String[] headers = {"Date", "Description", "Category", "Amount (Rs.)", "Receipt"};
        List<String[]> rows = new ArrayList<>();
        for (FinanceEntry fe : entries) {
            rows.add(new String[]{
                    DateUtil.format(fe.getDate()), fe.getDescription(),
                    fe.getCategory().name(), String.format("%.2f", fe.getAmount()),
                    fe.hasReceipt() ? "Yes" : "No"
            });
        }

        try {
            ExportService.export(strategy, headers, rows,
                    selectedSociety.getName() + " — Finance Report",
                    fc.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this,
                    "Report exported to:\n" + fc.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Export failed: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

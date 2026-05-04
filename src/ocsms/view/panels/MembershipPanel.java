package ocsms.view.panels;

import ocsms.controller.MembershipController;
import ocsms.model.*;
import ocsms.model.Membership.MembershipStatus;
import ocsms.model.User.UserRole;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * VIEW (MVC) — MembershipPanel
 * UC-03: Students can apply to societies with motivation statement + live char counter.
 * UC-04: Society Admin approves/rejects with mandatory remarks dialog.
 */
public class MembershipPanel extends JPanel {

    private final MembershipController ctrl = new MembershipController();
    private final User currentUser = SessionManager.getInstance().getCurrentUser();

    private JTable membershipTable;
    private DefaultTableModel tableModel;

    public MembershipPanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Membership Management"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Tab View for different roles ────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_CARD);
        tabs.setForeground(UITheme.TEXT);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBorder(new EmptyBorder(0, 20, 20, 20));

        UserRole role = currentUser.getRole();

        if (role == UserRole.MEMBER || role == UserRole.UNIVERSITY_ADMIN) {
            tabs.addTab("My Applications", buildMyApplicationsTab());
            tabs.addTab("Apply to Society",  buildApplyTab());
        }
        if (role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN) {
            tabs.addTab("Pending Applications", buildPendingTab());
            tabs.addTab("All Applications", buildAllApplicationsTab());
        }

        add(tabs, BorderLayout.CENTER);
    }

    // ── My Applications Tab ─────────────────────────────────────────────────────
    private JPanel buildMyApplicationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"Society", "Applied On", "Status", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Membership> myMems = ctrl.getMembershipsForStudent(currentUser.getId());
        for (Membership m : myMems) {
            Society society = DataStore.getInstance().findSocietyById(m.getSocietyId());
            model.addRow(new Object[]{
                    society != null ? society.getName() : "—",
                    DateUtil.format(m.getAppliedAt()),
                    m.getStatus().name(),
                    m.getRemarks() != null ? m.getRemarks() : "—"
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        applyStatusRenderer(table, 2);

        panel.add(UITheme.scrollPane(table), BorderLayout.CENTER);

        if (myMems.isEmpty()) {
            JLabel empty = new JLabel("You have not applied to any society yet.", SwingConstants.CENTER);
            empty.setForeground(UITheme.TEXT_DIM);
            empty.setFont(UITheme.FONT_BODY);
            panel.add(empty, BorderLayout.CENTER);
        }

        return panel;
    }

    // ── Apply Tab ───────────────────────────────────────────────────────────────
    private JPanel buildApplyTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Society selection
        JPanel topRow = new JPanel(new BorderLayout(12, 0));
        topRow.setBackground(UITheme.BG_CARD);

        JComboBox<Society> societyCombo = new JComboBox<>();
        UITheme.styleCombo(societyCombo);
        DataStore.getInstance().getSocieties().stream()
                .filter(s -> s.getStatus() == Society.SocietyStatus.ACTIVE)
                .forEach(societyCombo::addItem);

        topRow.add(UITheme.label("Select Society:"), BorderLayout.WEST);
        topRow.add(societyCombo, BorderLayout.CENTER);
        panel.add(topRow, BorderLayout.NORTH);

        // Motivation area + char counter
        JPanel midPanel = new JPanel(new BorderLayout(0, 6));
        midPanel.setBackground(UITheme.BG_CARD);

        JLabel motLabel = UITheme.label("Motivation Statement (50 – 500 characters):");
        JTextArea motArea = UITheme.textArea(6, 40);
        JLabel charCounter = new JLabel("0 / 500", SwingConstants.RIGHT);
        charCounter.setFont(UITheme.FONT_SMALL);
        charCounter.setForeground(UITheme.TEXT_DIM);

        motArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                int len = motArea.getText().length();
                charCounter.setText(len + " / 500");
                charCounter.setForeground(len >= 50 && len <= 500 ? UITheme.SUCCESS : UITheme.DANGER);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        midPanel.add(motLabel, BorderLayout.NORTH);
        midPanel.add(UITheme.scrollPane(motArea), BorderLayout.CENTER);
        midPanel.add(charCounter, BorderLayout.SOUTH);
        panel.add(midPanel, BorderLayout.CENTER);

        // Submit button
        JButton applyBtn = UITheme.accentButton("Submit Application");
        applyBtn.addActionListener(e -> {
            Society selected = (Society) societyCombo.getSelectedItem();
            if (selected == null) { showError("Please select a society."); return; }
            String error = ctrl.applyForMembership(
                    currentUser.getId(), selected.getId(), motArea.getText());
            if (error != null) { showError(error); }
            else {
                JOptionPane.showMessageDialog(this,
                        "Application submitted successfully! You will be notified of the decision.",
                        "Application Sent", JOptionPane.INFORMATION_MESSAGE);
                motArea.setText("");
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setBackground(UITheme.BG_CARD);
        btnRow.add(applyBtn);
        panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }

    // ── Pending Applications Tab (Admin) ────────────────────────────────────────
    private JPanel buildPendingTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"#", "Roll No", "Name", "Society", "Applied On", "Motivation (preview)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Find society managed by this admin
        String adminId = currentUser.getId();
        List<Membership> pending;
        if (currentUser.getRole() == UserRole.UNIVERSITY_ADMIN) {
            pending = ctrl.getAllMemberships().stream()
                    .filter(m -> m.getStatus() == MembershipStatus.PENDING)
                    .toList();
        } else {
            // Get society of this admin
            Society adminSociety = DataStore.getInstance().getSocieties().stream()
                    .filter(s -> s.getMemberIds().contains(adminId))
                    .findFirst().orElse(null);
            pending = adminSociety != null ?
                    ctrl.getPendingApplications(adminSociety.getId()) : List.of();
        }

        int i = 1;
        for (Membership m : pending) {
            User student = DataStore.getInstance().findUserById(m.getStudentId());
            Society society = DataStore.getInstance().findSocietyById(m.getSocietyId());
            String preview = m.getMotivationStatement().length() > 50 ?
                    m.getMotivationStatement().substring(0, 50) + "…" : m.getMotivationStatement();
            model.addRow(new Object[]{
                    i++,
                    student != null ? student.getRollNumber() : "—",
                    student != null ? student.getName() : "—",
                    society  != null ? society.getName() : "—",
                    DateUtil.format(m.getAppliedAt()),
                    preview
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnRow.setBackground(UITheme.BG_CARD);

        JButton approveBtn = UITheme.successButton("✓ Approve");
        JButton rejectBtn  = UITheme.dangerButton("✗ Reject");

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Please select an application."); return; }
            Membership m = pending.get(row);
            String err = ctrl.approveMembership(m.getId());
            if (err != null) showError(err);
            else {
                model.removeRow(row);
                JOptionPane.showMessageDialog(panel, "Application approved successfully!",
                        "Approved", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Please select an application."); return; }
            String remarks = JOptionPane.showInputDialog(panel,
                    "Enter rejection reason (required):", "Rejection Remarks", JOptionPane.WARNING_MESSAGE);
            if (remarks == null || remarks.isBlank()) {
                showError("Rejection remarks are required."); return;
            }
            Membership m = pending.get(row);
            String err = ctrl.rejectMembership(m.getId(), remarks);
            if (err != null) showError(err);
            else {
                model.removeRow(row);
                JOptionPane.showMessageDialog(panel, "Application rejected.",
                        "Rejected", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRow.add(approveBtn);
        btnRow.add(rejectBtn);

        panel.add(UITheme.scrollPane(table), BorderLayout.CENTER);
        panel.add(btnRow, BorderLayout.SOUTH);

        if (pending.isEmpty()) {
            JLabel lbl = new JLabel("No pending applications at this time.", SwingConstants.CENTER);
            lbl.setForeground(UITheme.TEXT_DIM);
            panel.add(lbl, BorderLayout.CENTER);
        }

        return panel;
    }

    // ── All Applications Tab (Admin) ────────────────────────────────────────────
    private JPanel buildAllApplicationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"Roll No", "Name", "Society", "Status", "Applied On", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Membership m : ctrl.getAllMemberships()) {
            User student = DataStore.getInstance().findUserById(m.getStudentId());
            Society society = DataStore.getInstance().findSocietyById(m.getSocietyId());
            model.addRow(new Object[]{
                    student != null ? student.getRollNumber() : "—",
                    student != null ? student.getName() : "—",
                    society  != null ? society.getName() : "—",
                    m.getStatus().name(),
                    DateUtil.format(m.getAppliedAt()),
                    m.getRemarks() != null ? m.getRemarks() : "—"
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        applyStatusRenderer(table, 3);

        panel.add(UITheme.scrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private void applyStatusRenderer(JTable table, int col) {
        table.getColumnModel().getColumn(col).setCellRenderer(
                new UITheme.DarkTableRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object val,
                            boolean sel, boolean focus, int row, int column) {
                        super.getTableCellRendererComponent(t, val, sel, focus, row, column);
                        if (val != null) {
                            String s = val.toString();
                            if (s.contains("APPROVED")) setForeground(UITheme.SUCCESS);
                            else if (s.contains("REJECTED")) setForeground(UITheme.DANGER);
                            else setForeground(UITheme.WARNING);
                        }
                        return this;
                    }
                });
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

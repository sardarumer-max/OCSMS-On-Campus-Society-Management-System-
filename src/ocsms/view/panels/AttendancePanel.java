package ocsms.view.panels;

import ocsms.controller.AttendanceController;
import ocsms.model.AttendanceRecord;
import ocsms.model.AttendanceRecord.AttendanceStatus;
import ocsms.model.Event;
import ocsms.model.User;
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
 * VIEW (MVC) — AttendancePanel
 * UC-10: Admin marks each registered member as Present/Absent with toggle buttons.
 * Row colours: green (Present), red (Absent). Attendance % shown in footer.
 * UC-17: Certificate auto-generated on marking Present (handled by controller).
 */
public class AttendancePanel extends JPanel {

    private final AttendanceController ctrl = new AttendanceController();

    private JComboBox<Event> eventCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel percentLabel;
    private List<User> members;

    public AttendancePanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Attendance Tracking"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Event selector
        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        selectorRow.setBackground(UITheme.BG);

        eventCombo = new JComboBox<>();
        UITheme.styleCombo(eventCombo);
        eventCombo.setPreferredSize(new Dimension(320, 34));
        DataStore.getInstance().getEvents().forEach(eventCombo::addItem);

        JButton loadBtn = UITheme.ghostButton("Load Members");
        loadBtn.addActionListener(e -> loadEventMembers());

        selectorRow.add(UITheme.label("Event:"));
        selectorRow.add(eventCombo);
        selectorRow.add(loadBtn);
        content.add(selectorRow, BorderLayout.NORTH);

        // Table
        String[] cols = {"Roll No", "Name", "Status", "Mark Present", "Mark Absent"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Custom status renderer with green/red rows
        table.getColumnModel().getColumn(2).setCellRenderer(new UITheme.DarkTableRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                Object status = t.getModel().getValueAt(r, 2);
                if ("PRESENT".equals(status)) {
                    setBackground(new Color(0x5DF8D8)); // Success C4
                    setForeground(UITheme.SUCCESS);
                } else if ("ABSENT".equals(status)) {
                    setBackground(UITheme.DANGER);
                    setForeground(UITheme.DANGER);
                } else {
                    setBackground(r % 2 == 0 ? UITheme.ROW_EVEN : UITheme.ROW_ODD);
                    setForeground(UITheme.TEXT_DIM);
                }
                return this;
            }
        });

        content.add(UITheme.scrollPane(table), BorderLayout.CENTER);

        // Footer with buttons and percentage
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.BG_CARD);
        footer.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(UITheme.BG_CARD);

        JButton presentBtn  = UITheme.successButton("Mark Present");
        JButton absentBtn   = UITheme.dangerButton("Mark Absent");
        JButton allPresentBtn = UITheme.ghostButton("Mark All Present");

        presentBtn.addActionListener(e -> markSelected(AttendanceStatus.PRESENT));
        absentBtn.addActionListener(e -> markSelected(AttendanceStatus.ABSENT));
        allPresentBtn.addActionListener(e -> markAll(AttendanceStatus.PRESENT));

        btnRow.add(presentBtn);
        btnRow.add(absentBtn);
        btnRow.add(allPresentBtn);

        percentLabel = new JLabel("Attendance: —%", SwingConstants.RIGHT);
        percentLabel.setFont(UITheme.FONT_BOLD);
        percentLabel.setForeground(UITheme.ACCENT);

        footer.add(btnRow, BorderLayout.WEST);
        footer.add(percentLabel, BorderLayout.EAST);

        content.add(footer, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
    }

    private void loadEventMembers() {
        Event event = (Event) eventCombo.getSelectedItem();
        if (event == null) return;

        tableModel.setRowCount(0);
        members = event.getRegisteredMemberIds().stream()
                .map(id -> DataStore.getInstance().findUserById(id))
                .filter(u -> u != null)
                .toList();

        for (User u : members) {
            AttendanceStatus status = ctrl.getStatus(event.getId(), u.getId());
            tableModel.addRow(new Object[]{
                    u.getRollNumber(), u.getName(),
                    status != null ? status.name() : "NOT MARKED",
                    "Present", "Absent"
            });
        }

        refreshPercentage(event);
    }

    private void markSelected(AttendanceStatus status) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a member row."); return; }
        Event event = (Event) eventCombo.getSelectedItem();
        if (event == null || members == null || row >= members.size()) return;

        User member = members.get(row);
        ctrl.markAttendance(event.getId(), member.getId(), status);
        tableModel.setValueAt(status.name(), row, 2);
        table.repaint();
        refreshPercentage(event);
    }

    private void markAll(AttendanceStatus status) {
        Event event = (Event) eventCombo.getSelectedItem();
        if (event == null || members == null) return;
        for (int i = 0; i < members.size(); i++) {
            ctrl.markAttendance(event.getId(), members.get(i).getId(), status);
            tableModel.setValueAt(status.name(), i, 2);
        }
        table.repaint();
        refreshPercentage(event);
    }

    private void refreshPercentage(Event event) {
        double pct = ctrl.getAttendancePercentage(event.getId());
        percentLabel.setText(String.format("Attendance: %.1f%%", pct));
        percentLabel.setForeground(pct >= 75 ? UITheme.SUCCESS : (pct >= 50 ? UITheme.WARNING : UITheme.DANGER));
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

package ocsms.view.panels;

import ocsms.controller.BookingController;
import ocsms.model.Booking;
import ocsms.model.Booking.ResourceType;
import ocsms.model.Society;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * VIEW (MVC) — BookingPanel
 * UC-14: Book venues/resources with 24h advance rule and conflict checking.
 */
public class BookingPanel extends JPanel {

    private final BookingController ctrl = new BookingController();
    private DefaultTableModel tableModel;
    private JTable table;

    public BookingPanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Resource Bookings"), BorderLayout.WEST);

        JButton createBtn = UITheme.accentButton("+ New Booking");
        createBtn.addActionListener(e -> showCreateBookingDialog());
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPnl.setBackground(UITheme.BG);
        btnPnl.add(createBtn);
        header.add(btnPnl, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(UITheme.BG_CARD);
        content.setBorder(new EmptyBorder(16, 24, 24, 24));

        String[] cols = {"Resource Type", "Resource Name", "Society", "Date & Time", "Duration (hrs)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Status renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new UITheme.DarkTableRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if ("CONFIRMED".equals(v)) setForeground(UITheme.SUCCESS);
                else if ("CANCELLED".equals(v)) setForeground(UITheme.DANGER);
                else setForeground(UITheme.WARNING);
                return this;
            }
        });

        content.add(UITheme.scrollPane(table), BorderLayout.CENTER);

        // Cancel button
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.setBackground(UITheme.BG_CARD);
        JButton cancelBtn = UITheme.dangerButton("Cancel Booking");
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showError("Select a booking to cancel."); return; }
            List<Booking> list = ctrl.getAllBookings(); // same order as table
            if (row < list.size()) {
                Booking b = list.get(row);
                if (b.getStatus() == Booking.BookingStatus.CANCELLED) {
                    showError("Already cancelled."); return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "Cancel this booking?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ctrl.cancelBooking(b.getId());
                    refreshTable();
                }
            }
        });
        south.add(cancelBtn);
        content.add(south, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Booking b : ctrl.getAllBookings()) {
            Society soc = DataStore.getInstance().findSocietyById(b.getSocietyId());
            tableModel.addRow(new Object[]{
                    b.getResourceType().name(),
                    b.getResourceName(),
                    soc != null ? soc.getName() : "—",
                    DateUtil.format(b.getDateTime()),
                    b.getDurationHours(),
                    b.getStatus().name()
            });
        }
    }

    private void showCreateBookingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Booking", true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridy = 0; form.add(UITheme.label("Resource Type"), gbc);
        gbc.gridy++;
        JComboBox<ResourceType> typeCombo = new JComboBox<>(ResourceType.values());
        UITheme.styleCombo(typeCombo);
        form.add(typeCombo, gbc);

        gbc.gridy++; form.add(UITheme.label("Resource Name (e.g. Auditorium A) *"), gbc);
        gbc.gridy++; JTextField nameF = UITheme.textField(""); form.add(nameF, gbc);

        gbc.gridy++; form.add(UITheme.label("Date (days from now) *"), gbc);
        gbc.gridy++; JSpinner daysSp = UITheme.spinner(new SpinnerNumberModel(2, 1, 365, 1)); form.add(daysSp, gbc);

        gbc.gridy++; form.add(UITheme.label("Start Hour (0-23) *"), gbc);
        gbc.gridy++; JSpinner hourSp = UITheme.spinner(new SpinnerNumberModel(10, 0, 23, 1)); form.add(hourSp, gbc);

        gbc.gridy++; form.add(UITheme.label("Duration (hours) *"), gbc);
        gbc.gridy++; JSpinner durSp = UITheme.spinner(new SpinnerNumberModel(2.0, 0.5, 12.0, 0.5)); form.add(durSp, gbc);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(UITheme.BG_CARD);
        JButton cancel = UITheme.ghostButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        JButton save = UITheme.accentButton("Book Resource");

        save.addActionListener(e -> {
            Society adminSociety = DataStore.getInstance().getSocieties().stream()
                    .filter(s -> s.getMemberIds().contains(SessionManager.getInstance().getCurrentUser().getId()))
                    .findFirst().orElse(null);

            if (adminSociety == null) { showError("You are not part of any society."); return; }

            int d = (int) daysSp.getValue();
            int h = (int) hourSp.getValue();
            double dur = (double) durSp.getValue();
            LocalDateTime dt = LocalDateTime.now().plusDays(d).withHour(h).withMinute(0);

            String err = ctrl.createBooking((ResourceType) typeCombo.getSelectedItem(),
                    nameF.getText().trim(), adminSociety.getId(), dt, dur);

            if (err != null) {
                // Warning dialog for conflicts
                JOptionPane.showMessageDialog(dialog, err, "Booking Conflict", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Booking confirmed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshTable();
            }
        });

        btnRow.add(cancel);
        btnRow.add(save);
        dialog.add(btnRow, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

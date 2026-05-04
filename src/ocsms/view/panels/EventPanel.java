package ocsms.view.panels;

import ocsms.controller.EventController;
import ocsms.model.*;
import ocsms.model.Event.EventStatus;
import ocsms.model.Event.EventType;
import ocsms.model.User.UserRole;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;
import ocsms.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import ocsms.model.Event;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/**
 * VIEW (MVC) — EventPanel
 * UC-06: Society Admin creates events with venue conflict detection.
 * UC-07: Members register for events.
 * UC-19: Joint event creation with co-society selection.
 */
public class EventPanel extends JPanel {

    private final EventController ctrl = new EventController();
    private final User currentUser = SessionManager.getInstance().getCurrentUser();
    private final MainFrame mainFrame;

    private DefaultTableModel tableModel;
    private JTable eventTable;
    private List<Event> currentEvents;

    public EventPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Events"), BorderLayout.WEST);

        if (currentUser.getRole() == UserRole.SOCIETY_ADMIN || currentUser.getRole() == UserRole.UNIVERSITY_ADMIN) {
            JButton createBtn = UITheme.accentButton("+ Create Event");
            createBtn.addActionListener(e -> showCreateEventDialog());
            JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPnl.setBackground(UITheme.BG);
            btnPnl.add(createBtn);
            header.add(btnPnl, BorderLayout.EAST);
        }
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_CARD);
        tabs.setForeground(UITheme.TEXT);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBorder(new EmptyBorder(0, 20, 20, 20));

        tabs.addTab("All Events", buildEventsTable(ctrl.getAllEvents()));
        tabs.addTab("Upcoming",   buildEventsTable(ctrl.getUpcomingEvents()));
        if (currentUser.getRole() == UserRole.MEMBER) {
            tabs.addTab("My Registrations", buildEventsTable(ctrl.getRegisteredEvents(currentUser.getId())));
        }

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildEventsTable(List<Event> events) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"Title", "Society", "Date & Time", "Venue", "Type", "Capacity", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Event e : events) {
            Society soc = DataStore.getInstance().findSocietyById(e.getSocietyId());
            model.addRow(new Object[]{
                    e.getTitle(),
                    soc != null ? soc.getName() : "—",
                    DateUtil.format(e.getDateTime()),
                    e.getVenue(),
                    e.getEventType() != null ? e.getEventType().name() : "—",
                    e.getRegisteredMemberIds().size() + " / " + e.getCapacity(),
                    e.getStatus().name()
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Status colour renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new UITheme.DarkTableRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null) {
                    switch (v.toString()) {
                        case "UPCOMING"  -> setForeground(UITheme.SUCCESS);
                        case "CANCELLED" -> setForeground(UITheme.DANGER);
                        case "PAST"      -> setForeground(UITheme.TEXT_DIM);
                        default          -> setForeground(UITheme.WARNING);
                    }
                }
                return this;
            }
        });

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnRow.setBackground(UITheme.BG_CARD);

        if (currentUser.getRole() == UserRole.MEMBER) {
            JButton regBtn = UITheme.accentButton("Register for Event");
            regBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) { showError("Please select an event."); return; }
                Event ev = events.get(row);
                int conf = JOptionPane.showConfirmDialog(this,
                        "Register for \"" + ev.getTitle() + "\"?",
                        "Confirm Registration", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    String err = ctrl.registerForEvent(currentUser.getId(), ev.getId());
                    if (err != null) showError(err);
                    else JOptionPane.showMessageDialog(this,
                            "Successfully registered for " + ev.getTitle() + "!",
                            "Registered", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            JButton fbBtn = UITheme.ghostButton("Give Feedback");
            fbBtn.addActionListener(e -> mainFrame.showCard(MainFrame.CARD_FEEDBACK));

            btnRow.add(regBtn);
            btnRow.add(fbBtn);
        }

        if (currentUser.getRole() == UserRole.SOCIETY_ADMIN || currentUser.getRole() == UserRole.UNIVERSITY_ADMIN) {
            JButton cancelBtn = UITheme.dangerButton("Cancel Event");
            cancelBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) { showError("Please select an event."); return; }
                Event ev = events.get(row);
                int conf = JOptionPane.showConfirmDialog(this,
                        "Cancel event \"" + ev.getTitle() + "\"? All registrants will be notified.",
                        "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (conf == JOptionPane.YES_OPTION) {
                    String err = ctrl.cancelEvent(ev.getId());
                    if (err != null) showError(err);
                    else {
                        model.setValueAt("CANCELLED", row, 6);
                        JOptionPane.showMessageDialog(this, "Event cancelled. Members have been notified.",
                                "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            JButton attBtn = UITheme.ghostButton("Mark Attendance");
            attBtn.addActionListener(e -> mainFrame.showCard(MainFrame.CARD_ATTENDANCE));

            btnRow.add(cancelBtn);
            btnRow.add(attBtn);
        }

        panel.add(UITheme.scrollPane(table), BorderLayout.CENTER);
        if (btnRow.getComponentCount() > 0) panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }

    // ── Create Event Dialog ─────────────────────────────────────────────────────
    private void showCreateEventDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Create New Event", true);
        dialog.setSize(480, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_CARD);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = UITheme.gbc();

        // Title
        gbc.gridy = 0; form.add(UITheme.label("Event Title *"), gbc);
        gbc.gridy++; JTextField titleF = UITheme.textField(""); form.add(titleF, gbc);

        // Society Selection
        gbc.gridy++; form.add(UITheme.label("Select Society *"), gbc);
        gbc.gridy++;
        JComboBox<Society> societyCombo = new JComboBox<>();
        for (Society s : DataStore.getInstance().getSocieties()) {
            societyCombo.addItem(s);
        }
        // Custom renderer for society dropdown
        societyCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Society) {
                    setText(((Society) value).getName());
                }
                return this;
            }
        });
        UITheme.styleCombo(societyCombo);
        form.add(societyCombo, gbc);

        // Date & Time (spinner)
        gbc.gridy++; form.add(UITheme.label("Date (days from now)"), gbc);
        gbc.gridy++;
        JSpinner daysSpinner = UITheme.spinner(new SpinnerNumberModel(7, 2, 365, 1));
        form.add(daysSpinner, gbc);

        // Hour
        gbc.gridy++; form.add(UITheme.label("Start Hour (0-23)"), gbc);
        gbc.gridy++;
        JSpinner hourSpinner = UITheme.spinner(new SpinnerNumberModel(10, 0, 23, 1));
        form.add(hourSpinner, gbc);

        // Venue
        gbc.gridy++; form.add(UITheme.label("Venue *"), gbc);
        gbc.gridy++; JTextField venueF = UITheme.textField("e.g. Auditorium A"); form.add(venueF, gbc);

        // Capacity
        gbc.gridy++; form.add(UITheme.label("Capacity *"), gbc);
        gbc.gridy++;
        JSpinner capSpinner = UITheme.spinner(new SpinnerNumberModel(50, 1, 1000, 10));
        form.add(capSpinner, gbc);

        // Event Type
        gbc.gridy++; form.add(UITheme.label("Event Type"), gbc);
        gbc.gridy++;
        JComboBox<EventType> typeCombo = new JComboBox<>(EventType.values());
        UITheme.styleCombo(typeCombo);
        form.add(typeCombo, gbc);

        // Poster
        gbc.gridy++; form.add(UITheme.label("Poster Image (optional, .jpg/.png, max 5MB)"), gbc);
        gbc.gridy++;
        JPanel posterRow = new JPanel(new BorderLayout(8, 0));
        posterRow.setBackground(UITheme.BG_CARD);
        JTextField posterField = UITheme.textField("No file selected");
        posterField.setEditable(false);
        JButton browseBtn = UITheme.ghostButton("Browse");
        final String[] posterPath = {null};
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select Poster Image");
            if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                if (f.length() > 5L * 1024 * 1024) {
                    showError("File exceeds 5MB limit."); return;
                }
                posterPath[0] = f.getAbsolutePath();
                posterField.setText(f.getName());
            }
        });
        posterRow.add(posterField, BorderLayout.CENTER);
        posterRow.add(browseBtn, BorderLayout.EAST);
        form.add(posterRow, gbc);

        // Joint event checkbox
        gbc.gridy++;
        JCheckBox jointCheck = new JCheckBox("Make this a Joint Event");
        jointCheck.setFont(UITheme.FONT_BODY);
        jointCheck.setForeground(UITheme.TEXT);
        jointCheck.setBackground(UITheme.BG_CARD);
        form.add(jointCheck, gbc);

        dialog.add(UITheme.scrollPane(form), BorderLayout.CENTER);

        // ── Buttons ────────────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(UITheme.BG_CARD);

        JButton cancelBtn = UITheme.ghostButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton createBtn = UITheme.accentButton("Create Event");
        createBtn.addActionListener(e -> {
            int days  = (int) daysSpinner.getValue();
            int hour  = (int) hourSpinner.getValue();
            int cap   = (int) capSpinner.getValue();
            LocalDateTime dt = LocalDateTime.now().plusDays(days).withHour(hour).withMinute(0);

            Society selectedSociety = (Society) societyCombo.getSelectedItem();
            if (selectedSociety == null) {
                showError("Please select a society for this event.");
                return;
            }

            String err = ctrl.createEvent(
                    titleF.getText().trim(), dt, venueF.getText().trim(),
                    cap, (EventType) typeCombo.getSelectedItem(),
                    selectedSociety.getId(), posterPath[0], jointCheck.isSelected(), null);

            if (err != null) showError(err);
            else {
                JOptionPane.showMessageDialog(dialog,
                        "Event created successfully! Society members have been notified.",
                        "Event Created", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(createBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

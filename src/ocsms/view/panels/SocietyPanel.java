package ocsms.view.panels;

import ocsms.controller.SocietyController;
import ocsms.model.Society;
import ocsms.model.Society.SocietyCategory;
import ocsms.model.Society.SocietyStatus;
import ocsms.model.User;
import ocsms.model.User.UserRole;
import ocsms.util.DataStore;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;
import ocsms.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * VIEW (MVC) — SocietyPanel
 * UC-11: View society profiles.
 * UC-05: Create society (Admin only).
 * UC-18: Archive society (University Admin only).
 */
public class SocietyPanel extends JPanel {

    private final SocietyController ctrl = new SocietyController();
    private final User currentUser = SessionManager.getInstance().getCurrentUser();
    private final MainFrame mainFrame;
    private JPanel listPanel;

    public SocietyPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Societies & Clubs"), BorderLayout.WEST);

        if (currentUser.getRole() == UserRole.UNIVERSITY_ADMIN || currentUser.getRole() == UserRole.FACULTY_ADVISOR) {
            JButton createBtn = UITheme.accentButton("+ New Society");
            createBtn.addActionListener(e -> showCreateSocietyDialog());
            JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPnl.setBackground(UITheme.BG);
            btnPnl.add(createBtn);
            header.add(btnPnl, BorderLayout.EAST);
        }
        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.setBorder(new EmptyBorder(0, 24, 24, 24));
        wrapper.add(UITheme.scrollPane(listPanel), BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        refreshList();
    }

    private void refreshList() {
        listPanel.removeAll();
        List<Society> societies = ctrl.getAllSocieties();

        for (Society s : societies) {
            JPanel card = buildSocietyCard(s);
            card.setAlignmentX(LEFT_ALIGNMENT);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(14));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildSocietyCard(Society society) {
        JPanel card = new JPanel(new BorderLayout(16, 10));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel icon = new JLabel("[" + (society.getCategory() != null ? society.getCategory().name().charAt(0) : "S") + "]");
        icon.setFont(new Font("SansSerif", Font.BOLD, 32));
        icon.setForeground(UITheme.ACCENT);
        icon.setBorder(new EmptyBorder(10, 20, 10, 20));
        card.add(icon, BorderLayout.WEST);

        // Center: Details
        JPanel details = new JPanel(new GridLayout(4, 1, 0, 4));
        details.setBackground(UITheme.BG_CARD);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setBackground(UITheme.BG_CARD);
        JLabel name = new JLabel(society.getName());
        name.setFont(UITheme.FONT_TITLE);
        name.setForeground(UITheme.ACCENT);
        titleRow.add(name);

        if (society.getStatus() == SocietyStatus.ARCHIVED) {
            JLabel archived = new JLabel("  [ARCHIVED]");
            archived.setFont(UITheme.FONT_BOLD);
            archived.setForeground(UITheme.DANGER);
            titleRow.add(archived);
        }
        details.add(titleRow);

        JLabel cat = UITheme.label("Category: " + society.getCategory() +
                "  |  Members: " + society.getMemberIds().size() + " / " + society.getCapacity());
        details.add(cat);

        JLabel desc = new JLabel(society.getDescription());
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(UITheme.TEXT);
        details.add(desc);

        card.add(details, BorderLayout.CENTER);

        // East / South actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(UITheme.BG_CARD);

        if (currentUser.getRole() == UserRole.UNIVERSITY_ADMIN && society.getStatus() == SocietyStatus.ACTIVE) {
            JButton archiveBtn = UITheme.dangerButton("Archive Society");
            archiveBtn.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(this, "Enter reason for archiving:");
                if (reason != null && !reason.isBlank()) {
                    int c = JOptionPane.showConfirmDialog(this,
                            "Archive society? All events will be cancelled.", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (c == JOptionPane.YES_OPTION) {
                        ctrl.archiveSociety(society.getId(), reason);
                        refreshList();
                    }
                }
            });
            actions.add(archiveBtn);
        }

        if (currentUser.getRole() == UserRole.MEMBER && society.getStatus() == SocietyStatus.ACTIVE) {
            if (!society.getMemberIds().contains(currentUser.getId())) {
                JButton joinBtn = UITheme.accentButton("Join Society");
                joinBtn.addActionListener(e -> mainFrame.showCard(MainFrame.CARD_MEMBERSHIP));
                actions.add(joinBtn);
            }
        }

        if (actions.getComponentCount() > 0) {
            card.add(actions, BorderLayout.SOUTH);
        }

        return card;
    }

    private void showCreateSocietyDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Society", true);
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridy = 0; form.add(UITheme.label("Society Name *"), gbc);
        gbc.gridy++; JTextField nameF = UITheme.textField(""); form.add(nameF, gbc);

        gbc.gridy++; form.add(UITheme.label("Category"), gbc);
        gbc.gridy++; JComboBox<SocietyCategory> catCombo = new JComboBox<>(SocietyCategory.values());
        UITheme.styleCombo(catCombo); form.add(catCombo, gbc);

        gbc.gridy++; form.add(UITheme.label("Capacity *"), gbc);
        gbc.gridy++; JSpinner capSp = UITheme.spinner(new SpinnerNumberModel(50, 10, 500, 10)); form.add(capSp, gbc);

        gbc.gridy++; form.add(UITheme.label("Description *"), gbc);
        gbc.gridy++; JTextArea descA = UITheme.textArea(4, 30); form.add(UITheme.scrollPane(descA), gbc);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(UITheme.BG_CARD);
        JButton cancel = UITheme.ghostButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        JButton save = UITheme.accentButton("Create Society");

        save.addActionListener(e -> {
            String err = ctrl.createSociety(nameF.getText(), (SocietyCategory) catCombo.getSelectedItem(),
                    descA.getText(), currentUser.getId(), (int) capSp.getValue());
            if (err != null) JOptionPane.showMessageDialog(dialog, err, "Error", JOptionPane.ERROR_MESSAGE);
            else {
                JOptionPane.showMessageDialog(dialog, "Created successfully!");
                dialog.dispose();
                refreshList();
            }
        });

        btnRow.add(cancel); btnRow.add(save);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}

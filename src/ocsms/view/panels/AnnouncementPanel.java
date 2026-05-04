package ocsms.view.panels;

import ocsms.controller.AnnouncementController;
import ocsms.model.Announcement;
import ocsms.model.Society;
import ocsms.model.User.UserRole;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * VIEW (MVC) — AnnouncementPanel
 * UC-08: Admin composes with 2000-char counter + Draft/Publish toggle.
 * Members see scrollable published announcements.
 */
public class AnnouncementPanel extends JPanel {

    private final AnnouncementController ctrl = new AnnouncementController();
    private final UserRole role = SessionManager.getInstance().getCurrentUser().getRole();

    public AnnouncementPanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Notice Board"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_CARD);
        tabs.setForeground(UITheme.TEXT);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBorder(new EmptyBorder(0, 20, 20, 20));

        tabs.addTab("Announcements", buildNoticeBoardTab());
        if (role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN) {
            tabs.addTab("Compose", buildComposeTab());
        }

        add(tabs, BorderLayout.CENTER);
    }

    // ── Notice Board (all users) ────────────────────────────────────────────────
    private JPanel buildNoticeBoardTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG_CARD);

        List<Announcement> anns = ctrl.getAllPublished();
        if (anns.isEmpty()) {
            JLabel empty = new JLabel("No announcements published yet.", SwingConstants.CENTER);
            empty.setForeground(UITheme.TEXT_DIM);
            empty.setFont(UITheme.FONT_BODY);
            listPanel.add(empty);
        }

        for (Announcement a : anns) {
            Society soc = DataStore.getInstance().findSocietyById(a.getSocietyId());
            JPanel card = buildAnnouncementCard(a, soc);
            card.setAlignmentX(LEFT_ALIGNMENT);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }

        panel.add(UITheme.scrollPane(listPanel), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAnnouncementCard(Announcement a, Society soc) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UITheme.BG);
        card.setBorder(UITheme.cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(UITheme.BG);

        JLabel titleLabel = new JLabel("📢 " + a.getTitle());
        titleLabel.setFont(UITheme.FONT_BOLD);
        titleLabel.setForeground(UITheme.ACCENT);

        JLabel metaLabel = new JLabel(
                (soc != null ? soc.getName() : "—") + "  ·  " + DateUtil.format(a.getPublishedAt()),
                SwingConstants.RIGHT);
        metaLabel.setFont(UITheme.FONT_SMALL);
        metaLabel.setForeground(UITheme.TEXT_DIM);

        titleRow.add(titleLabel, BorderLayout.WEST);
        titleRow.add(metaLabel, BorderLayout.EAST);
        card.add(titleRow, BorderLayout.NORTH);

        // Body preview (first 200 chars)
        String preview = a.getBody().length() > 200 ? a.getBody().substring(0, 200) + "…" : a.getBody();
        JLabel bodyLabel = new JLabel("<html><body style='width:100%'>" + preview + "</body></html>");
        bodyLabel.setFont(UITheme.FONT_BODY);
        bodyLabel.setForeground(UITheme.TEXT);
        card.add(bodyLabel, BorderLayout.CENTER);

        // Read More
        JButton readMore = UITheme.ghostButton("Read More");
        readMore.addActionListener(e -> JOptionPane.showMessageDialog(
                this, "<html><body style='width:400px'>" + a.getBody() + "</body></html>",
                a.getTitle(), JOptionPane.INFORMATION_MESSAGE));
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPnl.setBackground(UITheme.BG);
        btnPnl.add(readMore);
        card.add(btnPnl, BorderLayout.SOUTH);

        return card;
    }

    // ── Compose Tab (Admin only) ────────────────────────────────────────────────
    private JPanel buildComposeTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Society selector
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setBackground(UITheme.BG_CARD);
        JComboBox<Society> socCombo = new JComboBox<>();
        UITheme.styleCombo(socCombo);
        DataStore.getInstance().getSocieties().forEach(socCombo::addItem);
        topRow.add(UITheme.label("Society:"));
        topRow.add(socCombo);
        panel.add(topRow, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridy = 0; form.add(UITheme.label("Title *"), gbc);
        gbc.gridy++; JTextField titleF = UITheme.textField(""); form.add(titleF, gbc);

        gbc.gridy++; form.add(UITheme.label("Body * (max 2000 characters)"), gbc);

        gbc.gridy++;
        JTextArea bodyArea = UITheme.textArea(8, 40);
        JLabel charCounter = new JLabel("0 / 2000", SwingConstants.RIGHT);
        charCounter.setFont(UITheme.FONT_SMALL);
        charCounter.setForeground(UITheme.TEXT_DIM);

        bodyArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                int len = bodyArea.getText().length();
                charCounter.setText(len + " / 2000");
                charCounter.setForeground(len > 2000 ? UITheme.DANGER : UITheme.TEXT_DIM);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        form.add(UITheme.scrollPane(bodyArea), gbc);
        gbc.gridy++; form.add(charCounter, gbc);

        panel.add(form, BorderLayout.CENTER);

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnRow.setBackground(UITheme.BG_CARD);

        JToggleButton draftToggle = new JToggleButton("Save as Draft");
        draftToggle.setFont(UITheme.FONT_BODY);
        draftToggle.setBackground(UITheme.BG_HOVER);
        draftToggle.setForeground(UITheme.TEXT);
        draftToggle.setFocusPainted(false);
        draftToggle.setBorder(new EmptyBorder(8, 14, 8, 14));

        JButton publishBtn = UITheme.accentButton("Publish");
        publishBtn.addActionListener(e -> {
            Society soc = (Society) socCombo.getSelectedItem();
            boolean isDraft = draftToggle.isSelected();
            String err = ctrl.saveAnnouncement(
                    soc != null ? soc.getId() : "",
                    titleF.getText().trim(),
                    bodyArea.getText().trim(),
                    isDraft);
            if (err != null) {
                JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        isDraft ? "Announcement saved as draft." : "Announcement published to all members!",
                        isDraft ? "Saved" : "Published", JOptionPane.INFORMATION_MESSAGE);
                titleF.setText(""); bodyArea.setText("");
            }
        });

        btnRow.add(draftToggle);
        btnRow.add(publishBtn);
        panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }
}

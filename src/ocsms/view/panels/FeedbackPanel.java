package ocsms.view.panels;

import ocsms.controller.FeedbackController;
import ocsms.model.Event;
import ocsms.model.Feedback;
import ocsms.model.User.UserRole;
import ocsms.util.DataStore;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * VIEW (MVC) — FeedbackPanel
 * UC-12: Submit event feedback with star rating and anonymous option.
 * Admins see aggregated bar chart.
 */
public class FeedbackPanel extends JPanel {

    private final FeedbackController ctrl = new FeedbackController();
    private final UserRole role = SessionManager.getInstance().getCurrentUser().getRole();

    public FeedbackPanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Event Feedback"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_CARD);
        tabs.setForeground(UITheme.TEXT);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBorder(new EmptyBorder(0, 20, 20, 20));

        if (role == UserRole.MEMBER) {
            tabs.addTab("Submit Feedback", buildSubmitTab());
        }
        if (role == UserRole.SOCIETY_ADMIN || role == UserRole.FACULTY_ADVISOR) {
            tabs.addTab("View Ratings", buildAdminTab());
        }

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildSubmitTab() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridy = 0; form.add(UITheme.label("Select Event"), gbc);
        gbc.gridy++;
        JComboBox<Event> evCombo = new JComboBox<>();
        UITheme.styleCombo(evCombo);
        DataStore.getInstance().getEvents().stream()
                .filter(e -> e.getStatus() == Event.EventStatus.PAST)
                .forEach(evCombo::addItem);
        form.add(evCombo, gbc);

        gbc.gridy++; form.add(UITheme.label("Rating (1-5 Stars)"), gbc);
        gbc.gridy++;
        JPanel stars = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        stars.setBackground(UITheme.BG_CARD);
        ButtonGroup bg = new ButtonGroup();
        JRadioButton[] rbs = new JRadioButton[5];
        for (int i=0; i<5; i++) {
            rbs[i] = new JRadioButton((i+1) + " Star" + (i > 0 ? "s" : ""));
            rbs[i].setFont(UITheme.FONT_BODY);
            rbs[i].setForeground(UITheme.TEXT);
            rbs[i].setBackground(UITheme.BG_CARD);
            bg.add(rbs[i]);
            stars.add(rbs[i]);
        }
        rbs[4].setSelected(true); // default 5 star
        form.add(stars, gbc);

        gbc.gridy++; form.add(UITheme.label("Comments (optional)"), gbc);
        gbc.gridy++;
        JTextArea commArea = UITheme.textArea(4, 30);
        form.add(UITheme.scrollPane(commArea), gbc);

        gbc.gridy++;
        JCheckBox anonCheck = new JCheckBox("Submit Anonymously");
        anonCheck.setSelected(true);
        anonCheck.setForeground(UITheme.TEXT);
        anonCheck.setBackground(UITheme.BG_CARD);
        form.add(anonCheck, gbc);

        gbc.gridy++;
        JButton subBtn = UITheme.accentButton("Submit Feedback");
        subBtn.addActionListener(e -> {
            Event ev = (Event) evCombo.getSelectedItem();
            if (ev == null) {
                JOptionPane.showMessageDialog(this, "Select an event."); return;
            }
            int rating = 5;
            for (int i=0; i<5; i++) if (rbs[i].isSelected()) rating = i+1;

            String err = ctrl.submitFeedback(ev.getId(), SessionManager.getInstance().getCurrentUser().getId(),
                    rating, commArea.getText(), anonCheck.isSelected());

            if (err != null) JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE);
            else {
                JOptionPane.showMessageDialog(this, "Feedback submitted!");
                commArea.setText("");
            }
        });
        form.add(subBtn, gbc);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_CARD);
        p.add(form, BorderLayout.NORTH);
        return p;
    }

    private JPanel buildAdminTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(new EmptyBorder(16, 24, 24, 24));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(UITheme.BG_CARD);
        JComboBox<Event> evCombo = new JComboBox<>();
        UITheme.styleCombo(evCombo);
        DataStore.getInstance().getEvents().forEach(evCombo::addItem);
        top.add(UITheme.label("Event:"));
        top.add(evCombo);
        p.add(top, BorderLayout.NORTH);

        JPanel chartWrap = new JPanel(new BorderLayout());
        chartWrap.setBackground(UITheme.BG_CARD);

        JLabel avgLbl = UITheme.heading("Average Rating: —");
        avgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        chartWrap.add(avgLbl, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Event ev = (Event) evCombo.getSelectedItem();
                if (ev == null) return;
                int[] dist = ctrl.getRatingDistribution(ev.getId());
                int max = java.util.Arrays.stream(dist).max().orElse(1);
                if (max == 0) max = 1;

                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                int barW = (w - 100) / 5;
                for (int i=0; i<5; i++) {
                    int barH = (int)((double)dist[i] / max * (h - 40));
                    int x = 50 + i * (barW + 10);
                    int y = h - 20 - barH;
                    g2.setColor(UITheme.ACCENT);
                    g2.fillRect(x, y, barW, barH);
                    g2.setColor(UITheme.TEXT);
                    g2.drawString(dist[i]+" votes", x, y-5);
                    g2.drawString((i+1)+" Star", x, h-5);
                }
            }
        };
        chart.setPreferredSize(new Dimension(300, 200));
        chart.setBackground(UITheme.BG);
        chartWrap.add(chart, BorderLayout.CENTER);
        p.add(chartWrap, BorderLayout.CENTER);

        evCombo.addActionListener(e -> {
            Event ev = (Event) evCombo.getSelectedItem();
            if (ev != null) avgLbl.setText(String.format("Average Rating: %.1f / 5.0", ctrl.getAverageRating(ev.getId())));
            chart.repaint();
        });

        return p;
    }
}

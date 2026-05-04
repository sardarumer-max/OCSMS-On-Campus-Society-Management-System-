package ocsms.view.panels;

import ocsms.controller.AttendanceController;
import ocsms.controller.EventController;
import ocsms.controller.FinanceController;
import ocsms.controller.MembershipController;
import ocsms.model.*;
import ocsms.model.User.UserRole;
import ocsms.util.DataStore;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;
import ocsms.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import ocsms.model.Event;
import java.util.List;

/**
 * VIEW (MVC) — DashboardPanel
 * Displays KPI cards and summary charts for the current user's role.
 * UC-16: KPI cards for Faculty Advisor / University Admin.
 * Also the home landing for all other roles.
 */
public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final User currentUser = SessionManager.getInstance().getCurrentUser();

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG);
        setLayout(new BorderLayout(0, 0));
        buildUI();
    }

    private void buildUI() {
        // ── Page Header ──────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 16, 28));

        JLabel title = new JLabel("Welcome, " + currentUser.getName() + "!");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT);

        JLabel sub = new JLabel("Role: " + currentUser.getRole().name().replace("_", " ") +
                " · " + currentUser.getRollNumber());
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_DIM);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setBackground(UITheme.BG);
        titles.add(title);
        titles.add(sub);
        header.add(titles, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Main Content ─────────────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(0, 28, 28, 28));

        content.add(buildKPIRow(), BorderLayout.NORTH);
        content.add(buildChartAndRecent(), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JPanel buildKPIRow() {
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiRow.setBackground(UITheme.BG);

        int totalSocieties = (int) DataStore.getInstance().getSocieties().stream()
                .filter(s -> s.getStatus() == Society.SocietyStatus.ACTIVE).count();
        int totalEvents    = DataStore.getInstance().getEvents().size();
        int totalMembers   = DataStore.getInstance().getUsers().stream()
                .filter(u -> u.getRole() == UserRole.MEMBER).mapToInt(u -> 1).sum();
        int pendingApps    = (int) DataStore.getInstance().getMemberships().stream()
                .filter(m -> m.getStatus() == Membership.MembershipStatus.PENDING).count();

        kpiRow.add(UITheme.kpiCard("Active Societies", String.valueOf(totalSocieties), UITheme.ACCENT));
        kpiRow.add(UITheme.kpiCard("Total Events",     String.valueOf(totalEvents),    UITheme.SUCCESS));
        kpiRow.add(UITheme.kpiCard("Registered Users", String.valueOf(totalMembers),   UITheme.WARNING));
        kpiRow.add(UITheme.kpiCard("Pending Applications", String.valueOf(pendingApps), UITheme.DANGER));

        return kpiRow;
    }

    private JPanel buildChartAndRecent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(UITheme.BG);

        panel.add(buildBarChart());
        panel.add(buildRecentActivity());

        return panel;
    }

    /** Bar chart showing per-society member counts using Graphics2D */
    private JPanel buildBarChart() {
        JPanel chartCard = new JPanel(new BorderLayout());
        chartCard.setBackground(UITheme.BG_CARD);
        chartCard.setBorder(UITheme.cardBorder());

        JLabel chartTitle = UITheme.heading("Society Member Distribution");
        chartTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        chartCard.add(chartTitle, BorderLayout.NORTH);

        List<Society> societies = DataStore.getInstance().getSocieties();

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (societies.isEmpty()) return;

                int w = getWidth(), h = getHeight();
                int margin = 40, barWidth = (w - margin * 2) / societies.size() - 10;
                int maxMembers = societies.stream().mapToInt(s -> s.getMemberIds().size()).max().orElse(1);

                Color[] colors = {UITheme.ACCENT, UITheme.SUCCESS, UITheme.WARNING, UITheme.ACCENT2};

                for (int i = 0; i < societies.size(); i++) {
                    Society s = societies.get(i);
                    int members = s.getMemberIds().size();
                    int barH = maxMembers > 0 ? (int)((double) members / maxMembers * (h - margin * 2)) : 0;
                    int x = margin + i * (barWidth + 10);
                    int y = h - margin - barH;

                    // Bar
                    g2.setColor(colors[i % colors.length]);
                    g2.fillRoundRect(x, y, barWidth, barH, 6, 6);

                    // Value label
                    g2.setFont(UITheme.FONT_BOLD);
                    g2.setColor(UITheme.TEXT);
                    String val = String.valueOf(members);
                    int vx = x + (barWidth - g2.getFontMetrics().stringWidth(val)) / 2;
                    g2.drawString(val, vx, y - 4);

                    // Society name
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.setColor(UITheme.TEXT_DIM);
                    String nm = s.getName().length() > 10 ? s.getName().substring(0, 10) + "…" : s.getName();
                    int nx = x + (barWidth - g2.getFontMetrics().stringWidth(nm)) / 2;
                    g2.drawString(nm, nx, h - 6);
                }
            }
        };
        chart.setBackground(UITheme.BG_CARD);
        chart.setPreferredSize(new Dimension(300, 200));
        chartCard.add(chart, BorderLayout.CENTER);
        return chartCard;
    }

    /** Recent activity panel */
    private JPanel buildRecentActivity() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.cardBorder());

        JLabel cardTitle = UITheme.heading("Recent Events");
        card.add(cardTitle, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(UITheme.BG_CARD);

        List<Event> events = DataStore.getInstance().getEvents();
        int shown = 0;
        for (Event e : events) {
            if (shown++ >= 5) break;
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(UITheme.BG_CARD);
            row.setBorder(new EmptyBorder(6, 0, 6, 0));

            JLabel nameLabel = UITheme.valueLabel("• " + e.getTitle());
            nameLabel.setFont(UITheme.FONT_BODY);

            Society soc = DataStore.getInstance().findSocietyById(e.getSocietyId());
            JLabel socLabel = UITheme.label(soc != null ? soc.getName() : "—");

            row.add(nameLabel, BorderLayout.WEST);
            row.add(socLabel, BorderLayout.EAST);
            list.add(row);

            JSeparator sep = new JSeparator();
            sep.setForeground(new Color(0x3B7597)); // C2
            list.add(sep);
        }

        card.add(UITheme.scrollPane(list), BorderLayout.CENTER);

        // Quick nav buttons
        JPanel quickNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        quickNav.setBackground(UITheme.BG_CARD);
        JButton evBtn = UITheme.ghostButton("View Events");
        evBtn.addActionListener(e -> mainFrame.showCard(MainFrame.CARD_EVENTS));
        JButton memBtn = UITheme.ghostButton("Membership");
        memBtn.addActionListener(e -> mainFrame.showCard(MainFrame.CARD_MEMBERSHIP));
        quickNav.add(evBtn);
        quickNav.add(memBtn);
        card.add(quickNav, BorderLayout.SOUTH);

        return card;
    }
}

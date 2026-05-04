package ocsms.view;

import ocsms.controller.NotificationController;
import ocsms.model.Notification;
import ocsms.model.User;
import ocsms.model.User.UserRole;
import ocsms.pattern.observer.NotificationListener;
import ocsms.service.NotificationService;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;
import ocsms.view.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * VIEW (MVC) — MainFrame
 * The main application window after login.
 * Layout: BorderLayout — custom sidebar (WEST) + CardLayout content (CENTER) + toolbar (NORTH)
 * Sidebar buttons are shown/hidden based on current user role (UC-02).
 *
 * OBSERVER PATTERN: Implements NotificationListener to update bell badge on notification.
 */
public class MainFrame extends JFrame implements NotificationListener {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel   = new JPanel(cardLayout);
    private final NotificationController notifCtrl = new NotificationController();

    // Notification bell badge
    private JButton bellBtn;
    private int unreadCount = 0;

    // Current user
    private final User currentUser = SessionManager.getInstance().getCurrentUser();

    // ── Panel Card Names ────────────────────────────────────────────────────────
    public static final String CARD_DASHBOARD    = "Dashboard";
    public static final String CARD_MEMBERSHIP   = "Membership";
    public static final String CARD_EVENTS       = "Events";
    public static final String CARD_ANNOUNCEMENT = "Announcements";
    public static final String CARD_FINANCE      = "Finance";
    public static final String CARD_ATTENDANCE   = "Attendance";
    public static final String CARD_ELECTION     = "Elections";
    public static final String CARD_BOOKING      = "Bookings";
    public static final String CARD_SEARCH       = "Search";
    public static final String CARD_NOTIFICATION = "Notifications";
    public static final String CARD_SOCIETY      = "Societies";
    public static final String CARD_FEEDBACK     = "Feedback";
    public static final String CARD_CERTIFICATE  = "Certificates";

    public MainFrame() {
        setTitle("OCSMS — " + currentUser.getName() + " [" + currentUser.getRole() + "]");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 750));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { handleLogout(); }
        });

        NotificationService.getInstance().addListener(this);
        unreadCount = notifCtrl.getUnreadCount();

        buildUI();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        // Start idle timeout checker
        startIdleTimer();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);

        add(buildTopNav(), BorderLayout.NORTH);
        add(buildContentArea(), BorderLayout.CENTER);
    }

    // ── Top Navigation ──────────────────────────────────────────────────────────
    private JPanel buildTopNav() {
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setBackground(UITheme.SIDEBAR);
        topNav.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Top Row: App Name and User Info
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(UITheme.SIDEBAR);
        
        JLabel appName = new JLabel("OCSMS");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        appName.setForeground(UITheme.ACCENT);
        headerRow.add(appName, BorderLayout.WEST);

        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightInfo.setBackground(UITheme.SIDEBAR);

        JLabel userLabel = new JLabel(currentUser.getName() + "  [" + currentUser.getRole().name().replace("_", " ") + "]");
        userLabel.setFont(UITheme.FONT_SMALL);
        userLabel.setForeground(UITheme.TEXT_DIM);

        // Bell button
        bellBtn = new JButton("[N]") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (unreadCount > 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITheme.DANGER);
                    g2.fillOval(getWidth() - 14, 2, 13, 13);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    String badge = unreadCount > 9 ? "9+" : String.valueOf(unreadCount);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(badge, getWidth() - 14 + (13 - fm.stringWidth(badge)) / 2,
                            2 + 9 + (13 - fm.getHeight()) / 2);
                }
            }
        };
        bellBtn.setFont(UITheme.FONT_BOLD);
        bellBtn.setBackground(UITheme.SIDEBAR);
        bellBtn.setForeground(UITheme.ACCENT);
        bellBtn.setFocusPainted(false);
        bellBtn.setBorderPainted(false);
        bellBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bellBtn.addActionListener(e -> showCard(CARD_NOTIFICATION));

        JButton logoutBtn = UITheme.ghostButton("Logout");
        logoutBtn.addActionListener(e -> handleLogout());

        rightInfo.add(userLabel);
        rightInfo.add(bellBtn);
        rightInfo.add(logoutBtn);
        headerRow.add(rightInfo, BorderLayout.EAST);

        topNav.add(headerRow, BorderLayout.NORTH);

        // Bottom Row: Navigation Buttons
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navRow.setBackground(UITheme.SIDEBAR);
        
        UserRole role = currentUser.getRole();

        // ── Navigation buttons visible per role (UC-02) ──────────────────────
        addNav(navRow, "Dashboard",     CARD_DASHBOARD,    true);
        addNav(navRow, "Search",        CARD_SEARCH,       true);
        addNav(navRow, "Societies",     CARD_SOCIETY,      true);
        addNav(navRow, "Membership",    CARD_MEMBERSHIP,
                role == UserRole.MEMBER || role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN);
        addNav(navRow, "Events",        CARD_EVENTS,       true);
        addNav(navRow, "Announcements", CARD_ANNOUNCEMENT, true);
        addNav(navRow, "Finance",       CARD_FINANCE,
                role == UserRole.TREASURER || role == UserRole.FACULTY_ADVISOR || role == UserRole.SOCIETY_ADMIN);
        addNav(navRow, "Attendance",    CARD_ATTENDANCE,
                role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN);
        addNav(navRow, "Elections",     CARD_ELECTION,     true);
        addNav(navRow, "Bookings",      CARD_BOOKING,
                role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN);
        addNav(navRow, "Certificates",  CARD_CERTIFICATE,
                role == UserRole.MEMBER || role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN);
        addNav(navRow, "Feedback",      CARD_FEEDBACK,
                role == UserRole.MEMBER || role == UserRole.SOCIETY_ADMIN);
        addNav(navRow, "Notifications", CARD_NOTIFICATION, true);

        topNav.add(navRow, BorderLayout.SOUTH);

        return topNav;
    }

    private void addNav(JPanel parent, String text, String cardName, boolean visible) {
        if (!visible) return;
        JButton btn = UITheme.navButton(text);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> showCard(cardName));
        parent.add(btn);
    }

    // ── Content Area ─────────────────────────────────────────────────────────────
    private JPanel buildContentArea() {
        contentPanel.setBackground(UITheme.BG);

        // Register all panel cards
        contentPanel.add(new DashboardPanel(this),    CARD_DASHBOARD);
        contentPanel.add(new SearchPanel(),            CARD_SEARCH);
        contentPanel.add(new SocietyPanel(this),       CARD_SOCIETY);
        contentPanel.add(new MembershipPanel(),        CARD_MEMBERSHIP);
        contentPanel.add(new EventPanel(this),         CARD_EVENTS);
        contentPanel.add(new AnnouncementPanel(),      CARD_ANNOUNCEMENT);
        contentPanel.add(new FinancePanel(),           CARD_FINANCE);
        contentPanel.add(new AttendancePanel(),        CARD_ATTENDANCE);
        contentPanel.add(new ElectionPanel(),          CARD_ELECTION);
        contentPanel.add(new BookingPanel(),           CARD_BOOKING);
        contentPanel.add(new CertificatePanel(),       CARD_CERTIFICATE);
        contentPanel.add(new FeedbackPanel(),          CARD_FEEDBACK);
        contentPanel.add(new NotificationPanel(this),  CARD_NOTIFICATION);

        // Default to Dashboard
        cardLayout.show(contentPanel, CARD_DASHBOARD);
        return contentPanel;
    }

    // ── Public Navigation Method ─────────────────────────────────────────────────
    public void showCard(String cardName) {
        SessionManager.getInstance().updateActivity();
        cardLayout.show(contentPanel, cardName);
    }

    // ── OBSERVER: Called when a new Notification is dispatched ──────────────────
    @Override
    public void onNotification(Notification n) {
        if (n.getUserId().equals(currentUser.getId())) {
            unreadCount++;
            SwingUtilities.invokeLater(() -> bellBtn.repaint());
        }
    }

    /** Called by NotificationPanel to reset badge after reading */
    public void resetBadge() {
        unreadCount = 0;
        bellBtn.repaint();
    }

    // ── Logout ───────────────────────────────────────────────────────────────────
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            NotificationService.getInstance().removeListener(this);
            SessionManager.getInstance().logout();
            dispose();
            new LoginFrame();
        }
    }

    // ── Idle Timeout Timer ───────────────────────────────────────────────────────
    private void startIdleTimer() {
        Timer idleTimer = new Timer(60_000, e -> {
            if (SessionManager.getInstance().isIdleTimeout()) {
                JOptionPane.showMessageDialog(this,
                        "Session expired due to 30 minutes of inactivity. Please log in again.",
                        "Session Expired", JOptionPane.WARNING_MESSAGE);
                handleLogout();
            }
        });
        idleTimer.start();
    }
}

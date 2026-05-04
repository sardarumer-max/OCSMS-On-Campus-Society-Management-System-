package ocsms.view.panels;

import ocsms.controller.NotificationController;
import ocsms.model.Notification;
import ocsms.util.DateUtil;
import ocsms.util.UITheme;
import ocsms.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * VIEW (MVC) — NotificationPanel
 * UC-20: Displays list of system notifications for the current user.
 */
public class NotificationPanel extends JPanel {

    private final NotificationController ctrl = new NotificationController();
    private final MainFrame mainFrame;
    private JPanel listPanel;

    public NotificationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Notifications"), BorderLayout.WEST);

        JButton markAllBtn = UITheme.ghostButton("Mark All as Read");
        markAllBtn.addActionListener(e -> {
            ctrl.markAllRead();
            mainFrame.resetBadge();
            refreshList();
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(UITheme.BG);
        right.add(markAllBtn);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG_CARD);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_CARD);
        wrapper.setBorder(UITheme.cardBorder());
        wrapper.add(UITheme.scrollPane(listPanel), BorderLayout.CENTER);

        JPanel outerWrap = new JPanel(new BorderLayout());
        outerWrap.setBackground(UITheme.BG);
        outerWrap.setBorder(new EmptyBorder(0, 24, 24, 24));
        outerWrap.add(wrapper, BorderLayout.CENTER);

        add(outerWrap, BorderLayout.CENTER);
        refreshList();
    }

    public void refreshList() {
        listPanel.removeAll();
        List<Notification> notifs = ctrl.getMyNotifications();

        if (notifs.isEmpty()) {
            JLabel empty = new JLabel("You have no notifications.", SwingConstants.CENTER);
            empty.setForeground(UITheme.TEXT_DIM);
            empty.setFont(UITheme.FONT_BODY);
            empty.setBorder(new EmptyBorder(40, 0, 0, 0));
            listPanel.add(empty);
        } else {
            // Sort by latest first
            notifs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            for (Notification n : notifs) {
                JPanel card = new JPanel(new BorderLayout(12, 0));
                card.setBackground(n.isRead() ? UITheme.BG_CARD : new Color(0x0d3a57));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x3B7597)),
                        new EmptyBorder(16, 16, 16, 16)
                ));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                JLabel dot = new JLabel(n.isRead() ? "   " : "[NEW]");
                dot.setFont(UITheme.FONT_SMALL);
                dot.setForeground(UITheme.ACCENT);
                card.add(dot, BorderLayout.WEST);

                JPanel textP = new JPanel(new GridLayout(2, 1));
                textP.setBackground(card.getBackground());
                JLabel msg = new JLabel(n.getMessage());
                msg.setFont(n.isRead() ? UITheme.FONT_BODY : UITheme.FONT_BOLD);
                msg.setForeground(n.isRead() ? UITheme.TEXT : Color.WHITE);
                JLabel time = new JLabel(DateUtil.format(n.getCreatedAt()));
                time.setFont(UITheme.FONT_SMALL);
                time.setForeground(UITheme.TEXT_DIM);
                textP.add(msg);
                textP.add(time);
                card.add(textP, BorderLayout.CENTER);

                if (!n.isRead()) {
                    JButton readBtn = UITheme.ghostButton("Mark Read");
                    readBtn.addActionListener(e -> {
                        ctrl.markRead(n);
                        mainFrame.resetBadge();
                        refreshList();
                    });
                    JPanel r = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    r.setBackground(card.getBackground());
                    r.add(readBtn);
                    card.add(r, BorderLayout.EAST);
                }

                listPanel.add(card);
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}

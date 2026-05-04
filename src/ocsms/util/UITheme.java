package ocsms.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * UTILITY — UITheme
 * Centralised theme constants and factory helpers used by all View classes.
 * Colour Palette: #093C5D · #3B7597 · #6FD1D7 · #5DF8D8
 */
public final class UITheme {

    private UITheme() {}

    // ── Exact Colour Palette ────────────────────────────────────────────────────
    public static final Color C1 = new Color(0x093C5D); // Deep blue
    public static final Color C2 = new Color(0x3B7597); // Mid blue
    public static final Color C3 = new Color(0x6FD1D7); // Teal
    public static final Color C4 = new Color(0x5DF8D8); // Mint/Cyan

    // ── Theme Mapping ───────────────────────────────────────────────────────────
    public static final Color BG        = C1;
    public static final Color BG_CARD   = C2;
    public static final Color BG_HOVER  = C3;
    public static final Color SIDEBAR   = C1;
    public static final Color ACCENT    = C4;
    public static final Color ACCENT2   = C3;
    public static final Color TEXT      = Color.WHITE;
    public static final Color TEXT_DIM  = C4;
    public static final Color SUCCESS   = C4;
    public static final Color WARNING   = new Color(0xffb300); // Standard Warning
    public static final Color DANGER    = new Color(0xff5252); // Standard Danger
    public static final Color ROW_EVEN  = C2;
    public static final Color ROW_ODD   = C1;
    public static final Color TABLE_HDR = C3;

    // ── Fonts ───────────────────────────────────────────────────────────────────
    // Cross-platform fonts: SansSerif works on Windows, macOS, and Linux
    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD,  16);
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOLD    = new Font("SansSerif", Font.BOLD,  13);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);

    // ── Border Helpers ──────────────────────────────────────────────────────────
    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(C3, 1),
                new EmptyBorder(12, 16, 12, 16));
    }

    // ── Component Factories ─────────────────────────────────────────────────────

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_DIM);
        l.setBorder(new EmptyBorder(4, 0, 2, 0));
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEADING);
        l.setForeground(ACCENT);
        l.setBorder(new EmptyBorder(0, 0, 8, 0));
        return l;
    }

    public static JLabel valueLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT);
        return l;
    }

    public static JTextField textField(String placeholder) {
        JTextField f = new JTextField(placeholder, 20);
        f.setFont(FONT_BODY);
        f.setBackground(C1);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C3, 1),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField(20);
        f.setFont(FONT_BODY);
        f.setBackground(C1);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C3, 1),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    public static JTextArea textArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_BODY);
        ta.setBackground(C1);
        ta.setForeground(TEXT);
        ta.setCaretColor(ACCENT);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        return ta;
    }

    /** Primary accent button */
    public static JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(ACCENT);
        btn.setForeground(C1);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(9, 24, 9, 24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 36));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(C3);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C4, 2),
                        new EmptyBorder(7, 22, 7, 22)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
                btn.setBorder(new EmptyBorder(9, 24, 9, 24));
            }
        });
        return btn;
    }

    /** Danger button */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0xff7070));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(0xffaaaa), 1),
                        new EmptyBorder(7, 17, 7, 17)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(DANGER);
                btn.setBorder(new EmptyBorder(8, 18, 8, 18));
            }
        });
        return btn;
    }

    /** Success button */
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(C2);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(C4);
                btn.setForeground(C1);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C3, 1),
                        new EmptyBorder(7, 17, 7, 17)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(C2);
                btn.setForeground(TEXT);
                btn.setBorder(new EmptyBorder(8, 18, 8, 18));
            }
        });
        return btn;
    }

    /** Ghost / outline button */
    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(BG_CARD);
        btn.setForeground(ACCENT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT, 1),
                new EmptyBorder(7, 16, 7, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(C3);
                btn.setForeground(C1);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C4, 2),
                        new EmptyBorder(6, 15, 6, 15)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_CARD);
                btn.setForeground(ACCENT);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT, 1),
                        new EmptyBorder(7, 16, 7, 16)));
            }
        });
        return btn;
    }

    /** Top-nav horizontal button */
    public static JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT);
        btn.setBackground(SIDEBAR);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C2, 1, true),
                new EmptyBorder(5, 12, 5, 12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(C3);
                btn.setForeground(C1);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C4, 1, true),
                        new EmptyBorder(5, 12, 5, 12)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR);
                btn.setForeground(TEXT);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(C2, 1, true),
                        new EmptyBorder(5, 12, 5, 12)));
            }
        });
        return btn;
    }

    public static <T> void styleCombo(JComboBox<T> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(C1);
        combo.setForeground(TEXT);
        combo.setBorder(new LineBorder(C3, 1));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
    }

    public static JSpinner spinner(SpinnerModel model) {
        JSpinner sp = new JSpinner(model);
        sp.setFont(FONT_BODY);
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setBackground(C1);
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setForeground(TEXT);
        return sp;
    }

    /** Styles a JTable */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT);
        table.setBackground(ROW_EVEN);
        table.setSelectionBackground(C3);
        table.setSelectionForeground(C1);
        table.setGridColor(C2);
        table.setRowHeight(32);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(TABLE_HDR);
        table.getTableHeader().setForeground(C1);
        table.getTableHeader().setBorder(new LineBorder(C3, 1));
        table.setDefaultRenderer(Object.class, new DarkTableRenderer());
    }

    /** Wraps a component in a scroll pane */
    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_CARD);
        sp.setBorder(new LineBorder(C3, 1));
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        sp.getHorizontalScrollBar().setBackground(BG_CARD);
        return sp;
    }

    /** Standard GridBagConstraints */
    public static GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets = new Insets(3, 0, 3, 0);
        return g;
    }

    // ── Table Row Renderer ──────────────────────────────────────────────────────

    public static class DarkTableRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                setForeground(TEXT);
            } else {
                setBackground(C3);
                setForeground(C1);
            }
            setBorder(new EmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    // ── KPI Card ────────────────────────────────────────────────────────────────

    /** Creates a metric card for the dashboard */
    public static JPanel kpiCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 6));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(C3, 1),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel valLbl = new JLabel(value, SwingConstants.CENTER);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        valLbl.setForeground(valueColor);

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(FONT_SMALL);
        titleLbl.setForeground(C3);

        card.add(valLbl);
        card.add(titleLbl);
        return card;
    }
}

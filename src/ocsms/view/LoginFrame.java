package ocsms.view;

import ocsms.controller.AuthController;
import ocsms.model.User;
import ocsms.service.AuthService.LoginResult;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * VIEW (MVC) — LoginFrame
 * Entry point UI with two tabs: Login and Register.
 * UC-01: validates roll number, password, and role selection.
 * Dark theme with #0a0f1e background and #00d4ff accent.
 */
public class LoginFrame extends JFrame {

    private final AuthController authController = new AuthController();

    // ── Login Tab Fields ────────────────────────────────────────────────────────
    private JTabbedPane tabs;
    private JTextField loginRollField;
    private JPasswordField loginPassField;
    private JButton loginBtn;

    // ── Register Tab Fields ─────────────────────────────────────────────────────
    private JTextField regRollField, regNameField, regEmailField;
    private JPasswordField regPassField, regConfirmPassField;
    private JComboBox<User.UserRole> regRoleCombo;
    private JButton registerBtn;

    public LoginFrame() {
        setTitle("OCSMS — On-Campus Societies Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // ── Logo / Title Panel ───────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        headerPanel.setBackground(UITheme.BG);
        headerPanel.setBorder(new EmptyBorder(30, 20, 10, 20));

        JLabel logo = new JLabel("◈ OCSMS", SwingConstants.CENTER);
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(UITheme.ACCENT);

        JLabel subtitle = new JLabel("On-Campus Societies Management System", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_DIM);

        JLabel university = new JLabel("FAST-NUCES Peshawar", SwingConstants.CENTER);
        university.setFont(UITheme.FONT_SMALL);
        university.setForeground(UITheme.TEXT_DIM);

        headerPanel.add(logo);
        headerPanel.add(subtitle);
        headerPanel.add(university);
        add(headerPanel, BorderLayout.NORTH);

        // ── Tabbed Pane ──────────────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_CARD);
        tabs.setForeground(UITheme.TEXT);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBorder(new EmptyBorder(10, 20, 20, 20));

        tabs.addTab("  Login  ", buildLoginPanel());
        tabs.addTab("  Register  ", buildRegisterPanel());

        add(tabs, BorderLayout.CENTER);

        // ── Footer ───────────────────────────────────────────────────────────
        JLabel footer = new JLabel("SDA Project — Umer (24P-0557) & Sudais (24P-0572)", SwingConstants.CENTER);
        footer.setFont(UITheme.FONT_SMALL);
        footer.setForeground(UITheme.TEXT_DIM);
        footer.setBorder(new EmptyBorder(5, 0, 10, 0));
        footer.setBackground(UITheme.BG);
        footer.setOpaque(true);
        add(footer, BorderLayout.SOUTH);
    }

    // ── Login Panel ─────────────────────────────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(UITheme.label("Roll Number (e.g. 24P-0557)"), gbc);
        gbc.gridy++;
        loginRollField = UITheme.textField("24P-0557");
        panel.add(loginRollField, gbc);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(10), gbc);

        gbc.gridy++;
        panel.add(UITheme.label("Password"), gbc);
        gbc.gridy++;
        loginPassField = UITheme.passwordField();
        panel.add(loginPassField, gbc);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(16), gbc);

        gbc.gridy++;
        loginBtn = UITheme.accentButton("Login");
        loginBtn.addActionListener(this::handleLogin);
        // Allow pressing Enter in password field to login
        loginPassField.addActionListener(this::handleLogin);
        loginRollField.addActionListener(this::handleLogin);
        panel.add(loginBtn, gbc);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(12), gbc);

        // Demo credentials hint
        gbc.gridy++;
        JLabel hint = new JLabel("<html><center>Demo: admin / 00A-0000 · Password: Password1<br>" +
                "umer / 24P-0557 · student1 / 24P-0301</center></html>", SwingConstants.CENTER);
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_DIM);
        panel.add(hint, gbc);

        return panel;
    }

    // ── Register Panel ──────────────────────────────────────────────────────────
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));
        GridBagConstraints gbc = UITheme.gbc();

        gbc.gridy = 0;
        panel.add(UITheme.label("Full Name"), gbc);
        gbc.gridy++; regNameField = UITheme.textField(""); panel.add(regNameField, gbc);

        gbc.gridy++;
        panel.add(UITheme.label("Roll Number (format: 24P-0557)"), gbc);
        gbc.gridy++; regRollField = UITheme.textField(""); panel.add(regRollField, gbc);

        gbc.gridy++;
        panel.add(UITheme.label("Email"), gbc);
        gbc.gridy++; regEmailField = UITheme.textField(""); panel.add(regEmailField, gbc);

        gbc.gridy++;
        panel.add(UITheme.label("Role"), gbc);
        gbc.gridy++;
        regRoleCombo = new JComboBox<>(new User.UserRole[]{
                User.UserRole.MEMBER, User.UserRole.SOCIETY_ADMIN,
                User.UserRole.FACULTY_ADVISOR, User.UserRole.TREASURER, User.UserRole.UNIVERSITY_ADMIN
        });
        UITheme.styleCombo(regRoleCombo);
        panel.add(regRoleCombo, gbc);

        gbc.gridy++;
        panel.add(UITheme.label("Password (8+ chars, 1 digit)"), gbc);
        gbc.gridy++; regPassField = UITheme.passwordField(); panel.add(regPassField, gbc);

        gbc.gridy++;
        panel.add(UITheme.label("Confirm Password"), gbc);
        gbc.gridy++; regConfirmPassField = UITheme.passwordField(); panel.add(regConfirmPassField, gbc);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(10), gbc);

        gbc.gridy++;
        registerBtn = UITheme.accentButton("Create Account");
        registerBtn.addActionListener(this::handleRegister);
        panel.add(registerBtn, gbc);

        return panel;
    }

    // ── Action Handlers ─────────────────────────────────────────────────────────

    private void handleLogin(ActionEvent e) {
        String roll = loginRollField.getText().trim();
        String pass = new String(loginPassField.getPassword());

        LoginResult result = authController.login(roll, pass);

        switch (result.status) {
            case SUCCESS:
                dispose();
                new MainFrame();
                break;
            case ACCOUNT_LOCKED:
                JOptionPane.showMessageDialog(this, result.message,
                        "Account Locked", JOptionPane.WARNING_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(this, result.message,
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister(ActionEvent e) {
        String roll    = regRollField.getText().trim();
        String name    = regNameField.getText().trim();
        String email   = regEmailField.getText().trim();
        String pass    = new String(regPassField.getPassword());
        String confirm = new String(regConfirmPassField.getPassword());
        User.UserRole role = (User.UserRole) regRoleCombo.getSelectedItem();

        AuthController.RegisterResult result =
                authController.register(roll, name, email, pass, confirm, role);

        if (result.success) {
            JOptionPane.showMessageDialog(this, result.message,
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            // Clear fields
            regRollField.setText(""); regNameField.setText("");
            regEmailField.setText(""); regPassField.setText(""); regConfirmPassField.setText("");
            // Auto switch to login tab
            tabs.setSelectedIndex(0);
            loginRollField.setText(roll);
            loginPassField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, result.message,
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

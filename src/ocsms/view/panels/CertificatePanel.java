package ocsms.view.panels;

import ocsms.controller.AttendanceController;
import ocsms.model.Certificate;
import ocsms.model.Event;
import ocsms.model.Society;
import ocsms.model.User;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * VIEW (MVC) — CertificatePanel
 * UC-17: Download auto-generated certificates.
 */
public class CertificatePanel extends JPanel {

    private final AttendanceController ctrl = new AttendanceController();
    private final User currentUser = SessionManager.getInstance().getCurrentUser();

    public CertificatePanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("My Certificates"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UITheme.BG_CARD);
        content.setBorder(new EmptyBorder(16, 24, 24, 24));

        String[] cols = {"Event Name", "Society", "Date Issued", "Verification Code"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        List<Certificate> certs = ctrl.getCertificatesForMember(currentUser.getId());
        for (Certificate c : certs) {
            Event ev = DataStore.getInstance().findEventById(c.getEventId());
            Society soc = DataStore.getInstance().findSocietyById(c.getSocietyId());
            model.addRow(new Object[]{
                    ev != null ? ev.getTitle() : "—",
                    soc != null ? soc.getName() : "—",
                    DateUtil.format(c.getGeneratedAt()),
                    c.getVerificationCode()
            });
        }

        content.add(UITheme.scrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setBackground(UITheme.BG_CARD);
        JButton dlBtn = UITheme.accentButton("Download Certificate");
        dlBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a certificate to download."); return;
            }
            Certificate c = certs.get(row);
            Event ev = DataStore.getInstance().findEventById(c.getEventId());
            Society soc = DataStore.getInstance().findSocietyById(c.getSocietyId());
            downloadCertificate(c, ev, soc);
        });
        south.add(dlBtn);
        content.add(south, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
    }

    private void downloadCertificate(Certificate c, Event ev, Society soc) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(c.getVerificationCode() + ".txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter w = new PrintWriter(new FileWriter(fc.getSelectedFile()))) {
                w.println("=================================================");
                w.println("             CERTIFICATE OF ATTENDANCE           ");
                w.println("=================================================");
                w.println();
                w.println("This certifies that:");
                w.println("  " + currentUser.getName() + " (" + currentUser.getRollNumber() + ")");
                w.println();
                w.println("Has successfully attended:");
                w.println("  Event:   " + (ev != null ? ev.getTitle() : ""));
                w.println("  Society: " + (soc != null ? soc.getName() : ""));
                w.println("  Date:    " + (ev != null ? DateUtil.format(ev.getDateTime()) : ""));
                w.println();
                w.println("Verification Code: " + c.getVerificationCode());
                w.println("Issued On:         " + DateUtil.format(c.getGeneratedAt()));
                w.println("=================================================");
                JOptionPane.showMessageDialog(this, "Downloaded successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage());
            }
        }
    }
}

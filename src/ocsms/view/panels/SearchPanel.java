package ocsms.view.panels;

import ocsms.controller.SocietyController;
import ocsms.model.Society;
import ocsms.model.Society.SocietyCategory;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * VIEW (MVC) — SearchPanel
 * UC-13: Search and discovery of societies with instant filtering.
 */
public class SearchPanel extends JPanel {

    private final SocietyController ctrl = new SocietyController();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchF;
    private JComboBox<SocietyCategory> catCombo;
    private JToggleButton activeToggle;

    public SearchPanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));
        header.add(UITheme.heading("Discover Societies"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchBar.setBackground(UITheme.BG);

        searchF = UITheme.textField("Search keywords...");
        searchF.setPreferredSize(new Dimension(250, 34));

        catCombo = new JComboBox<>(SocietyCategory.values());
        UITheme.styleCombo(catCombo);
        catCombo.insertItemAt(null, 0); // "All Categories" option
        catCombo.setSelectedIndex(0);

        activeToggle = new JToggleButton("Active Only");
        activeToggle.setSelected(true);
        activeToggle.setBackground(UITheme.BG_HOVER);
        activeToggle.setForeground(UITheme.TEXT);
        activeToggle.setFocusPainted(false);
        activeToggle.setBorder(new EmptyBorder(6, 12, 6, 12));

        JButton searchBtn = UITheme.accentButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, 34));
        searchBtn.addActionListener(e -> doSearch());

        searchBar.add(searchF);
        searchBar.add(catCombo);
        searchBar.add(activeToggle);
        searchBar.add(searchBtn);
        content.add(searchBar, BorderLayout.NORTH);

        // Results Table
        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(UITheme.BG_CARD);
        tableWrap.setBorder(UITheme.cardBorder());

        String[] cols = {"Society Name", "Category", "Description (Preview)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Status coloring
        table.getColumnModel().getColumn(3).setCellRenderer(new UITheme.DarkTableRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if ("ACTIVE".equals(v)) setForeground(UITheme.SUCCESS);
                else setForeground(UITheme.TEXT_DIM);
                return this;
            }
        });

        tableWrap.add(UITheme.scrollPane(table), BorderLayout.CENTER);
        content.add(tableWrap, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
        doSearch(); // initial load
    }

    private void doSearch() {
        String kw = searchF.getText().trim();
        if (kw.equals("Search keywords...")) kw = "";
        SocietyCategory cat = (SocietyCategory) catCombo.getSelectedItem();
        boolean active = activeToggle.isSelected();

        List<Society> results = ctrl.searchSocieties(kw, cat, active);
        tableModel.setRowCount(0);

        for (Society s : results) {
            String desc = s.getDescription().length() > 60 ?
                    s.getDescription().substring(0, 60) + "..." : s.getDescription();
            tableModel.addRow(new Object[]{
                    s.getName(),
                    s.getCategory().name(),
                    desc,
                    s.getStatus().name()
            });
        }
    }
}

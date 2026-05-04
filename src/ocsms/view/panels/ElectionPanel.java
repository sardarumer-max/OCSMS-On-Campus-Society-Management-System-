package ocsms.view.panels;

import ocsms.controller.ElectionController;
import ocsms.model.Election;
import ocsms.model.Election.ElectionPhase;
import ocsms.model.Society;
import ocsms.model.User;
import ocsms.model.User.UserRole;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.SessionManager;
import ocsms.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * VIEW (MVC) — ElectionPanel
 * UC-15: Full election lifecycle:
 *   - University Admin / Society Admin: create election, nominate candidates, advance phases
 *   - Members: self-nominate OR vote once (radio buttons)
 *   - All: view results with winner announcement and vote tallies
 */
public class ElectionPanel extends JPanel {

    private final ElectionController ctrl = new ElectionController();
    private final User currentUser = SessionManager.getInstance().getCurrentUser();
    private final UserRole role = currentUser.getRole();

    public ElectionPanel() {
        setBackground(UITheme.BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG);
        header.setBorder(new EmptyBorder(24, 28, 12, 28));

        JLabel heading = UITheme.heading("Elections & Voting");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(heading, BorderLayout.WEST);

        // Create Election button for admins
        if (role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN) {
            JButton createBtn = UITheme.accentButton("+ Create Election");
            createBtn.addActionListener(e -> showCreateElectionDialog());
            JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPnl.setBackground(UITheme.BG);
            btnPnl.add(createBtn);
            header.add(btnPnl, BorderLayout.EAST);
        }
        add(header, BorderLayout.NORTH);

        // ── Info banner for members ──────────────────────────────────────────
        if (role == UserRole.MEMBER) {
            JLabel infoLbl = new JLabel("  You can self-nominate or cast your vote below during the appropriate phase.", SwingConstants.LEFT);
            infoLbl.setFont(UITheme.FONT_SMALL);
            infoLbl.setForeground(UITheme.ACCENT);
            infoLbl.setOpaque(true);
            infoLbl.setBackground(new Color(0x3B7597)); // C2
            infoLbl.setBorder(new EmptyBorder(6, 28, 6, 28));
            add(infoLbl, BorderLayout.NORTH);
        }

        add(buildElectionList(), BorderLayout.CENTER);
    }

    // ── Election Cards List ──────────────────────────────────────────────────────
    private JPanel buildElectionList() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.setBorder(new EmptyBorder(0, 24, 24, 24));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG);

        List<Election> elections = ctrl.getAllElections();

        if (elections.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(UITheme.BG_CARD);
            emptyPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
            JLabel empty = new JLabel("No elections found. " +
                    (role == UserRole.UNIVERSITY_ADMIN || role == UserRole.SOCIETY_ADMIN
                            ? "Click '+ Create Election' to start one." : "Check back later."),
                    SwingConstants.CENTER);
            empty.setForeground(UITheme.TEXT_DIM);
            empty.setFont(UITheme.FONT_BODY);
            emptyPanel.add(empty);
            listPanel.add(emptyPanel);
        }

        for (Election election : elections) {
            JPanel card = buildElectionCard(election);
            card.setAlignmentX(LEFT_ALIGNMENT);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(16));
        }

        wrapper.add(UITheme.scrollPane(listPanel), BorderLayout.CENTER);
        return wrapper;
    }

    // ── Single Election Card ─────────────────────────────────────────────────────
    private JPanel buildElectionCard(Election election) {
        Society soc = DataStore.getInstance().findSocietyById(election.getSocietyId());
        String socName = soc != null ? soc.getName() : "Unknown Society";

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x6FD1D7), 1), // C3
                new EmptyBorder(16, 20, 16, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        // ── Title row ────────────────────────────────────────────────────────
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(UITheme.BG_CARD);

        JLabel titleLbl = new JLabel("Election: " + election.getPosition() + "  —  " + socName);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(UITheme.ACCENT);

        JLabel phasePill = new JLabel("  " + election.getPhase().name() + "  ");
        phasePill.setFont(UITheme.FONT_BOLD);
        phasePill.setForeground(Color.BLACK);
        phasePill.setBackground(getPhaseColor(election.getPhase()));
        phasePill.setOpaque(true);
        phasePill.setBorder(new EmptyBorder(3, 10, 3, 10));

        topRow.add(titleLbl, BorderLayout.WEST);
        topRow.add(phasePill, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // ── Timeline info ─────────────────────────────────────────────────────
        JPanel timePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        timePanel.setBackground(UITheme.BG_CARD);
        timePanel.add(UITheme.label("Nominations:  " +
                DateUtil.format(election.getNominationStart()) + "  to  " +
                DateUtil.format(election.getNominationDeadline())));
        timePanel.add(UITheme.label("Voting:  " +
                DateUtil.format(election.getVotingStart()) + "  to  " +
                DateUtil.format(election.getVotingDeadline())));
        card.add(timePanel, BorderLayout.CENTER);

        // ── Action area ───────────────────────────────────────────────────────
        JPanel actionArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actionArea.setBackground(UITheme.BG_CARD);

        switch (election.getPhase()) {
            case NOMINATION -> buildNominationActions(election, actionArea);
            case VOTING     -> buildVotingActions(election, actionArea);
            case COMPLETED  -> buildResultsView(election, actionArea);
            case CANCELLED  -> actionArea.add(UITheme.label("This election has been cancelled."));
        }

        card.add(actionArea, BorderLayout.SOUTH);
        return card;
    }

    // ── NOMINATION Phase Actions ──────────────────────────────────────────────────
    private void buildNominationActions(Election election, JPanel area) {
        // Show current nominees
        List<String> nominees = election.getNominees();
        if (!nominees.isEmpty()) {
            JPanel nomineeList = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            nomineeList.setBackground(UITheme.BG_CARD);
            JLabel nomLbl = UITheme.label("Current Nominees: ");
            nomineeList.add(nomLbl);
            for (String nid : nominees) {
                User u = DataStore.getInstance().findUserById(nid);
                JLabel chip = new JLabel(u != null ? u.getName() : nid);
                chip.setFont(UITheme.FONT_SMALL);
                chip.setForeground(UITheme.TEXT);
                chip.setBackground(new Color(0x3B7597)); // C2
                chip.setOpaque(true);
                chip.setBorder(new EmptyBorder(2, 8, 2, 8));
                nomineeList.add(chip);
            }
            area.add(nomineeList);
        }

        // Members can self-nominate
        if (role == UserRole.MEMBER && !election.getNominees().contains(currentUser.getId())) {
            JButton nominateBtn = UITheme.accentButton("Submit My Nomination");
            nominateBtn.addActionListener(e -> {
                String err = ctrl.submitNomination(election.getId(), currentUser.getId());
                if (err != null) showError(err);
                else {
                    JOptionPane.showMessageDialog(this, "Your nomination was submitted successfully!",
                            "Nominated", JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                }
            });
            area.add(nominateBtn);
        } else if (role == UserRole.MEMBER && election.getNominees().contains(currentUser.getId())) {
            JLabel alreadyNomLbl = new JLabel("  You are nominated for this election.");
            alreadyNomLbl.setForeground(UITheme.SUCCESS);
            alreadyNomLbl.setFont(UITheme.FONT_BOLD);
            area.add(alreadyNomLbl);
        }

        // Admins can add candidates by roll number
        if (role == UserRole.UNIVERSITY_ADMIN || role == UserRole.SOCIETY_ADMIN) {
            JButton addCandBtn = UITheme.ghostButton("Add Candidate by Roll No");
            addCandBtn.addActionListener(e -> {
                String rollNum = JOptionPane.showInputDialog(this,
                        "Enter Roll Number of candidate to nominate:\n(e.g. 24P-0301)",
                        "Nominate Candidate", JOptionPane.QUESTION_MESSAGE);
                if (rollNum != null && !rollNum.isBlank()) {
                    User u = DataStore.getInstance().findUserByRollNumber(rollNum.trim());
                    if (u == null) {
                        showError("No user found with Roll Number: " + rollNum.trim() +
                                "\nRegistered demo users: 24P-0301, 24P-0302, 24P-0303");
                        return;
                    }
                    String err = ctrl.submitNomination(election.getId(), u.getId());
                    if (err != null) showError(err);
                    else {
                        JOptionPane.showMessageDialog(this,
                                u.getName() + " has been nominated successfully!",
                                "Candidate Added", JOptionPane.INFORMATION_MESSAGE);
                        refreshUI();
                    }
                }
            });
            area.add(addCandBtn);

            // Start Voting button
            JButton startVoteBtn = UITheme.successButton("Start Voting Phase");
            startVoteBtn.addActionListener(e -> {
                if (election.getNominees().isEmpty()) {
                    showError("No candidates nominated yet. Add at least one candidate before starting voting.");
                    return;
                }
                int c = JOptionPane.showConfirmDialog(this,
                        "Start voting phase? Nomination will be closed.\n" +
                        "Candidates: " + election.getNominees().size(),
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (c == JOptionPane.YES_OPTION) {
                    String err = ctrl.startVotingPhase(election.getId());
                    if (err != null) showError(err);
                    else {
                        JOptionPane.showMessageDialog(this, "Voting phase is now OPEN! Members can cast votes.",
                                "Voting Started", JOptionPane.INFORMATION_MESSAGE);
                        refreshUI();
                    }
                }
            });
            area.add(startVoteBtn);
        }
    }

    // ── VOTING Phase Actions ──────────────────────────────────────────────────────
    private void buildVotingActions(Election election, JPanel area) {
        List<String> nominees = election.getNominees();

        // Show vote counts for admins
        if (role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN || role == UserRole.FACULTY_ADVISOR) {
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            statsPanel.setBackground(UITheme.BG_CARD);
            JLabel statsLbl = UITheme.label("Live Vote Count: ");
            statsPanel.add(statsLbl);
            for (String nid : nominees) {
                User u = DataStore.getInstance().findUserById(nid);
                String name = u != null ? u.getName() : nid;
                int count = election.getVoteCount(nid);
                JLabel voteLbl = new JLabel(name + ": " + count + " vote(s)");
                voteLbl.setFont(UITheme.FONT_BOLD);
                voteLbl.setForeground(UITheme.WARNING);
                voteLbl.setBorder(new EmptyBorder(0, 8, 0, 8));
                statsPanel.add(voteLbl);
            }
            area.add(statsPanel);
        }

        // Member voting UI
        if (role == UserRole.MEMBER) {
            boolean alreadyVoted = election.getVotes().containsKey(currentUser.getId());
            if (alreadyVoted) {
                String myVote = election.getVotes().get(currentUser.getId());
                User votedFor = DataStore.getInstance().findUserById(myVote);
                JLabel voted = new JLabel("  You voted for: " + (votedFor != null ? votedFor.getName() : "Unknown"));
                voted.setFont(UITheme.FONT_BOLD);
                voted.setForeground(UITheme.SUCCESS);
                area.add(voted);
            } else if (nominees.isEmpty()) {
                area.add(UITheme.label("No candidates available to vote for yet."));
            } else {
                // Radio button voting
                JPanel votePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                votePanel.setBackground(UITheme.BG_CARD);
                votePanel.add(UITheme.label("Cast your vote: "));

                ButtonGroup bg = new ButtonGroup();
                List<JRadioButton> radioButtons = new ArrayList<>();

                for (String nid : nominees) {
                    User nominee = DataStore.getInstance().findUserById(nid);
                    String label = nominee != null ? nominee.getName() + " (" + nominee.getRollNumber() + ")" : nid;
                    JRadioButton rb = new JRadioButton(label);
                    rb.setFont(UITheme.FONT_BODY);
                    rb.setForeground(UITheme.TEXT);
                    rb.setBackground(UITheme.BG_CARD);
                    rb.putClientProperty("nomineeId", nid);
                    bg.add(rb);
                    radioButtons.add(rb);
                    votePanel.add(rb);
                }

                JButton voteBtn = UITheme.accentButton("Cast Vote");
                voteBtn.addActionListener(e -> {
                    String selectedId = radioButtons.stream()
                            .filter(AbstractButton::isSelected)
                            .map(rb -> (String) rb.getClientProperty("nomineeId"))
                            .findFirst().orElse(null);
                    if (selectedId == null) {
                        showError("Please select a candidate before voting.");
                        return;
                    }
                    User selectedUser = DataStore.getInstance().findUserById(selectedId);
                    int c = JOptionPane.showConfirmDialog(this,
                            "Confirm vote for: " + (selectedUser != null ? selectedUser.getName() : selectedId) +
                            "\nThis action cannot be undone.",
                            "Confirm Vote", JOptionPane.YES_NO_OPTION);
                    if (c == JOptionPane.YES_OPTION) {
                        String err = ctrl.castVote(election.getId(), currentUser.getId(), selectedId);
                        if (err != null) showError(err);
                        else {
                            JOptionPane.showMessageDialog(this,
                                    "Vote cast successfully! Thank you for participating.",
                                    "Vote Recorded", JOptionPane.INFORMATION_MESSAGE);
                            refreshUI();
                        }
                    }
                });
                votePanel.add(voteBtn);
                area.add(votePanel);
            }
        }

        // Admin: close election
        if (role == UserRole.SOCIETY_ADMIN || role == UserRole.UNIVERSITY_ADMIN || role == UserRole.FACULTY_ADVISOR) {
            JButton closeBtn = UITheme.dangerButton("Close Voting & Publish Results");
            closeBtn.addActionListener(e -> {
                int c = JOptionPane.showConfirmDialog(this,
                        "Close voting and publish final results?\nTotal votes: " + election.getVotes().size(),
                        "Close Election", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (c == JOptionPane.YES_OPTION) {
                    ctrl.closeElection(election.getId());
                    JOptionPane.showMessageDialog(this, "Election closed! Results have been published.",
                            "Results Published", JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                }
            });
            area.add(closeBtn);
        }
    }

    // ── COMPLETED Results View ────────────────────────────────────────────────────
    private void buildResultsView(Election election, JPanel area) {
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));

        String winnerId = election.getWinner();
        User winner = winnerId != null ? DataStore.getInstance().findUserById(winnerId) : null;

        // Winner banner
        JLabel winnerLbl = new JLabel(winner != null
                ? "WINNER: " + winner.getName() + " (" + winner.getRollNumber() + ")  —  " + election.getVoteCount(winnerId) + " votes"
                : "No votes cast — no winner declared.");
        winnerLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        winnerLbl.setForeground(UITheme.SUCCESS);
        winnerLbl.setBorder(new EmptyBorder(4, 0, 8, 0));
        winnerLbl.setAlignmentX(LEFT_ALIGNMENT);
        area.add(winnerLbl);

        // All candidates with vote tallies
        for (String nid : election.getNominees()) {
            User u = DataStore.getInstance().findUserById(nid);
            int votes = election.getVoteCount(nid);
            boolean isWinner = nid.equals(winnerId);

            JLabel row = new JLabel((isWinner ? "★  " : "   ") +
                    (u != null ? u.getName() : nid) + "  —  " + votes + " vote(s)");
            row.setFont(UITheme.FONT_BODY);
            row.setForeground(isWinner ? UITheme.SUCCESS : UITheme.TEXT_DIM);
            row.setAlignmentX(LEFT_ALIGNMENT);
            area.add(row);
        }

        if (election.getNominees().isEmpty()) {
            area.add(UITheme.label("No candidates participated."));
        }
    }

    // ── Create Election Dialog ─────────────────────────────────────────────────────
    private void showCreateElectionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Create New Election", true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_CARD);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = UITheme.gbc();

        // Position
        gbc.gridy = 0; form.add(UITheme.label("Position Name  (e.g. President)"), gbc);
        gbc.gridy++; JTextField posF = UITheme.textField("President"); form.add(posF, gbc);

        // Society
        gbc.gridy++; form.add(UITheme.label("Select Society"), gbc);
        gbc.gridy++;
        JComboBox<Society> societyCombo = new JComboBox<>();
        for (Society s : DataStore.getInstance().getSocieties()) societyCombo.addItem(s);
        societyCombo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, idx, sel, focus);
                if (value instanceof Society) setText(((Society) value).getName());
                return this;
            }
        });
        UITheme.styleCombo(societyCombo);
        form.add(societyCombo, gbc);

        // Nomination duration
        gbc.gridy++; form.add(UITheme.label("Nomination Period Length (days)"), gbc);
        gbc.gridy++;
        JSpinner nomLenSp = UITheme.spinner(new SpinnerNumberModel(5, 1, 30, 1));
        form.add(nomLenSp, gbc);

        // Voting duration
        gbc.gridy++; form.add(UITheme.label("Voting Period Length (days, starts after nominations)"), gbc);
        gbc.gridy++;
        JSpinner voteLenSp = UITheme.spinner(new SpinnerNumberModel(3, 1, 30, 1));
        form.add(voteLenSp, gbc);

        // Info note
        gbc.gridy++;
        JLabel note = new JLabel("<html><i>Nominations start immediately. You can add candidates after creation.</i></html>");
        note.setFont(UITheme.FONT_SMALL);
        note.setForeground(UITheme.TEXT_DIM);
        form.add(note, gbc);

        dialog.add(UITheme.scrollPane(form), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(UITheme.BG_CARD);

        JButton cancelBtn = UITheme.ghostButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton createBtn = UITheme.accentButton("Create Election");
        createBtn.addActionListener(e -> {
            Society sel = (Society) societyCombo.getSelectedItem();
            if (sel == null) { showError("Please select a society."); return; }
            String position = posF.getText().trim();
            if (position.isBlank()) { showError("Position name is required."); return; }

            int nomDays  = (int) nomLenSp.getValue();
            int voteDays = (int) voteLenSp.getValue();

            LocalDateTime nomStart  = LocalDateTime.now();
            LocalDateTime nomEnd    = nomStart.plusDays(nomDays);
            LocalDateTime voteStart = nomEnd.plusDays(1);
            LocalDateTime voteEnd   = voteStart.plusDays(voteDays);

            String err = ctrl.createElection(sel.getId(), position, nomStart, nomEnd, voteStart, voteEnd);
            if (err != null) { showError(err); return; }

            JOptionPane.showMessageDialog(dialog,
                    "Election created!\n\nNomination phase is now open.\n" +
                    "Use 'Add Candidate by Roll No' to nominate students.",
                    "Election Created", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            refreshUI();
        });

        btnRow.add(cancelBtn);
        btnRow.add(createBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────
    private void refreshUI() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private Color getPhaseColor(ElectionPhase phase) {
        return switch (phase) {
            case NOMINATION -> UITheme.WARNING;
            case VOTING     -> UITheme.ACCENT;
            case COMPLETED  -> UITheme.SUCCESS;
            case CANCELLED  -> UITheme.DANGER;
        };
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

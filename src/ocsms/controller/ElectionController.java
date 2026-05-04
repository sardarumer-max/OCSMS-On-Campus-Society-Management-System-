package ocsms.controller;

import ocsms.model.Election;
import ocsms.model.Election.ElectionPhase;
import ocsms.model.User;
import ocsms.pattern.factory.NotificationFactory;
import ocsms.service.NotificationService;
import ocsms.util.DataStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — ElectionController
 * Handles election creation, nominations, voting, and result publication.
 * Used in: UC-15
 */
public class ElectionController {

    /**
     * Creates a new election for a society position.
     * UC-15: voting period must start after nomination period ends.
     */
    public String createElection(String societyId, String position,
                                  LocalDateTime nomStart, LocalDateTime nomEnd,
                                  LocalDateTime voteStart, LocalDateTime voteEnd) {
        if (position == null || position.isBlank()) return "Position name is required.";
        if (nomStart == null || nomEnd == null || voteStart == null || voteEnd == null)
            return "All date/time fields are required.";
        if (!nomEnd.isAfter(nomStart))
            return "Nomination deadline must be after nomination start.";
        if (!voteStart.isAfter(nomEnd))
            return "Voting period must start after the nomination deadline.";
        if (!voteEnd.isAfter(voteStart))
            return "Voting deadline must be after voting start.";

        Election election = new Election(societyId, position, nomStart, nomEnd, voteStart, voteEnd);
        DataStore.getInstance().getElections().add(election);
        return null; // success
    }

    /**
     * Member submits their nomination for an election.
     * UC-15: only during nomination phase.
     */
    public String submitNomination(String electionId, String memberId) {
        Election election = DataStore.getInstance().findElectionById(electionId);
        if (election == null) return "Election not found.";
        if (election.getPhase() != ElectionPhase.NOMINATION)
            return "Nomination phase is not currently open.";
        if (election.getNominees().contains(memberId))
            return "You have already submitted your nomination.";

        election.getNominees().add(memberId);
        return null;
    }

    /**
     * Member casts a vote.
     * UC-15: enforces one-vote-per-member rule.
     */
    public String castVote(String electionId, String memberId, String nomineeId) {
        Election election = DataStore.getInstance().findElectionById(electionId);
        if (election == null) return "Election not found.";
        if (election.getPhase() != ElectionPhase.VOTING)
            return "Voting phase is not currently open.";
        if (election.getVotes().containsKey(memberId))
            return "You have already cast your vote in this election.";
        if (!election.getNominees().contains(nomineeId))
            return "Invalid nominee selection.";

        election.castVote(memberId, nomineeId);
        return null;
    }

    /**
     * Admin advances the election phase manually or closes the election.
     * Auto-publishes results; detects tie and notifies admin.
     */
    public String closeElection(String electionId) {
        Election election = DataStore.getInstance().findElectionById(electionId);
        if (election == null) return "Election not found.";

        election.setPhase(ElectionPhase.COMPLETED);

        String winner = election.getWinner();
        // Check for tie
        if (winner != null && election.getNominees().size() > 1) {
            int winnerVotes = election.getVoteCount(winner);
            long tied = election.getNominees().stream()
                    .filter(n -> election.getVoteCount(n) == winnerVotes).count();
            if (tied > 1) {
                // Notify society admin of tie
                notifyAdminOfTie(election);
            }
        }

        // Notify all society members of result
        User winningUser = null;
        if (winner != null) {
            winningUser = DataStore.getInstance().findUserById(winner);
            if (winningUser != null) {
                // Promote to President (SOCIETY_ADMIN)
                winningUser.setRole(User.UserRole.SOCIETY_ADMIN);
                // We should push this to Supabase if configured
                if (ocsms.util.SupabaseConfig.isConfigured()) {
                    ocsms.util.SupabaseClient.update("users", "id", winningUser.getId(), winningUser);
                }
            }
        }

        String winnerName = winningUser != null ? winningUser.getName() : "No winner (no votes cast)";

        DataStore.getInstance().getSocieties().stream()
                .filter(s -> s.getId().equals(election.getSocietyId()))
                .findFirst().ifPresent(society -> {
                    for (String memberId : society.getMemberIds()) {
                        NotificationService.getInstance().dispatch(
                                NotificationFactory.voteResult(memberId, society.getName(),
                                        election.getPosition(), winnerName));
                    }
                });

        return null;
    }

    /** Advances election to voting phase */
    public String startVotingPhase(String electionId) {
        Election election = DataStore.getInstance().findElectionById(electionId);
        if (election == null) return "Election not found.";
        if (election.getNominees().isEmpty())
            return "No nominees registered. Extend nomination period first.";
        election.setPhase(ElectionPhase.VOTING);
        return null;
    }

    private void notifyAdminOfTie(Election election) {
        DataStore.getInstance().getUsers().stream()
                .filter(u -> u.getRole() == User.UserRole.SOCIETY_ADMIN ||
                             u.getRole() == User.UserRole.FACULTY_ADVISOR)
                .forEach(admin -> NotificationService.getInstance().dispatch(
                        NotificationFactory.create(
                                ocsms.model.Notification.NotificationType.VOTE_RESULT,
                                "⚖️ TIE detected in " + election.getPosition() +
                                " election. Admin action required to resolve.",
                                admin.getId())));
    }

    /** Returns elections for a specific society */
    public List<Election> getElectionsForSociety(String societyId) {
        return DataStore.getInstance().getElections().stream()
                .filter(e -> e.getSocietyId().equals(societyId))
                .collect(Collectors.toList());
    }

    /** Returns all elections */
    public List<Election> getAllElections() {
        return DataStore.getInstance().getElections();
    }
}

package ocsms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MODEL CLASS — Election
 * Represents a society election including nominees and votes.
 */
import java.io.Serializable;
public class Election implements Serializable {

    public enum ElectionPhase { NOMINATION, VOTING, COMPLETED, CANCELLED }

    private String id;
    private String societyId;
    private String position;
    private LocalDateTime nominationDeadline;
    private LocalDateTime votingDeadline;
    private LocalDateTime nominationStart;
    private LocalDateTime votingStart;
    private List<String> nominees;              // User IDs of nominees
    private Map<String, String> votes;          // memberId → nomineeId
    private ElectionPhase phase;

    public Election() {
        this.id = UUID.randomUUID().toString();
        this.nominees = new ArrayList<>();
        this.votes = new HashMap<>();
        this.phase = ElectionPhase.NOMINATION;
    }

    public Election(String societyId, String position,
                    LocalDateTime nominationStart, LocalDateTime nominationDeadline,
                    LocalDateTime votingStart, LocalDateTime votingDeadline) {
        this();
        this.societyId = societyId;
        this.position = position;
        this.nominationStart = nominationStart;
        this.nominationDeadline = nominationDeadline;
        this.votingStart = votingStart;
        this.votingDeadline = votingDeadline;
    }

    /** Cast a vote — returns false if already voted */
    public boolean castVote(String memberId, String nomineeId) {
        if (votes.containsKey(memberId)) return false;
        votes.put(memberId, nomineeId);
        return true;
    }

    /** Returns vote count for a specific nominee */
    public int getVoteCount(String nomineeId) {
        return (int) votes.values().stream().filter(v -> v.equals(nomineeId)).count();
    }

    /** Returns the nominee ID with the most votes, or null if no votes */
    public String getWinner() {
        return nominees.stream()
                .max((a, b) -> Integer.compare(getVoteCount(a), getVoteCount(b)))
                .orElse(null);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSocietyId() { return societyId; }
    public void setSocietyId(String societyId) { this.societyId = societyId; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public LocalDateTime getNominationDeadline() { return nominationDeadline; }
    public void setNominationDeadline(LocalDateTime nominationDeadline) { this.nominationDeadline = nominationDeadline; }

    public LocalDateTime getVotingDeadline() { return votingDeadline; }
    public void setVotingDeadline(LocalDateTime votingDeadline) { this.votingDeadline = votingDeadline; }

    public LocalDateTime getNominationStart() { return nominationStart; }
    public void setNominationStart(LocalDateTime nominationStart) { this.nominationStart = nominationStart; }

    public LocalDateTime getVotingStart() { return votingStart; }
    public void setVotingStart(LocalDateTime votingStart) { this.votingStart = votingStart; }

    public List<String> getNominees() { return nominees; }
    public void setNominees(List<String> nominees) { this.nominees = nominees; }

    public Map<String, String> getVotes() { return votes; }
    public void setVotes(Map<String, String> votes) { this.votes = votes; }

    public ElectionPhase getPhase() { return phase; }
    public void setPhase(ElectionPhase phase) { this.phase = phase; }
}

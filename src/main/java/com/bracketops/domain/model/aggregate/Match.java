package com.bracketops.domain.model.aggregate;

import com.bracketops.domain.model.exception.DomainException;
import com.bracketops.domain.model.valueobject.MatchRound;
import com.bracketops.domain.model.valueobject.MatchStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Match {
    private final String id;
    private final String tournamentId;
    private final MatchRound round;
    private final int matchOrder;
    private String teamAId;
    private String teamAName;
    private String teamBId;
    private String teamBName;
    private int teamAScore;
    private int teamBScore;
    private String winnerTeamId;
    private String winnerTeamName;
    private MatchStatus status;
    private String nextMatchId;
    private String loserNextMatchId;
    private final Long version; // Optimistic locking version
    private final LocalDateTime scheduledTime;

    public Match(String id, String tournamentId, MatchRound round, int matchOrder,
                 String teamAId, String teamAName, String teamBId, String teamBName,
                 int teamAScore, int teamBScore, String winnerTeamId, String winnerTeamName,
                 MatchStatus status, String nextMatchId, Long version, LocalDateTime scheduledTime) {
        this(id, tournamentId, round, matchOrder, teamAId, teamAName, teamBId, teamBName,
             teamAScore, teamBScore, winnerTeamId, winnerTeamName, status, nextMatchId, null, version, scheduledTime);
    }

    public Match(String id, String tournamentId, MatchRound round, int matchOrder,
                 String teamAId, String teamAName, String teamBId, String teamBName,
                 int teamAScore, int teamBScore, String winnerTeamId, String winnerTeamName,
                 MatchStatus status, String nextMatchId, String loserNextMatchId, Long version, LocalDateTime scheduledTime) {
        if (tournamentId == null || tournamentId.trim().isEmpty()) {
            throw new DomainException("Tournament ID is required for a match");
        }

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.tournamentId = tournamentId.trim();
        this.round = round != null ? round : MatchRound.QUARTERFINALS;
        this.matchOrder = matchOrder;
        this.teamAId = teamAId;
        this.teamAName = teamAName != null ? teamAName : "TBD";
        this.teamBId = teamBId;
        this.teamBName = teamBName != null ? teamBName : "TBD";
        this.teamAScore = teamAScore;
        this.teamBScore = teamBScore;
        this.winnerTeamId = winnerTeamId;
        this.winnerTeamName = winnerTeamName;
        this.status = status != null ? status : MatchStatus.SCHEDULED;
        this.nextMatchId = nextMatchId;
        this.loserNextMatchId = loserNextMatchId;
        this.version = version;
        this.scheduledTime = scheduledTime != null ? scheduledTime : LocalDateTime.now();
    }

    public void updateScore(int scoreA, int scoreB) {
        if (scoreA < 0 || scoreB < 0) {
            throw new DomainException("Scores cannot be negative");
        }
        if (scoreA == scoreB) {
            throw new DomainException("Tournament matches cannot end in a draw. A winner must be declared.");
        }

        this.teamAScore = scoreA;
        this.teamBScore = scoreB;
        this.status = MatchStatus.COMPLETED;

        if (scoreA > scoreB) {
            this.winnerTeamId = teamAId;
            this.winnerTeamName = teamAName;
        } else {
            this.winnerTeamId = teamBId;
            this.winnerTeamName = teamBName;
        }
    }

    public void processWalkover(String disqualifiedTeamId) {
        this.status = MatchStatus.WALKOVER;
        if (disqualifiedTeamId != null && disqualifiedTeamId.equals(teamAId)) {
            this.teamAScore = 0;
            this.teamBScore = 2;
            this.winnerTeamId = teamBId;
            this.winnerTeamName = teamBName;
        } else {
            this.teamAScore = 2;
            this.teamBScore = 0;
            this.winnerTeamId = teamAId;
            this.winnerTeamName = teamAName;
        }
    }

    public void setTeamA(String teamId, String teamName) {
        this.teamAId = teamId;
        this.teamAName = teamName != null ? teamName : "TBD";
    }

    public void setTeamB(String teamId, String teamName) {
        this.teamBId = teamId;
        this.teamBName = teamName != null ? teamName : "TBD";
    }

    public String getId() { return id; }
    public String getTournamentId() { return tournamentId; }
    public MatchRound getRound() { return round; }
    public int getMatchOrder() { return matchOrder; }
    public String getTeamAId() { return teamAId; }
    public String getTeamAName() { return teamAName; }
    public String getTeamBId() { return teamBId; }
    public String getTeamBName() { return teamBName; }
    public int getTeamAScore() { return teamAScore; }
    public int getTeamBScore() { return teamBScore; }
    public String getWinnerTeamId() { return winnerTeamId; }
    public String getWinnerTeamName() { return winnerTeamName; }
    public MatchStatus getStatus() { return status; }
    public String getNextMatchId() { return nextMatchId; }
    public String getLoserNextMatchId() { return loserNextMatchId; }
    public Long getVersion() { return version; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

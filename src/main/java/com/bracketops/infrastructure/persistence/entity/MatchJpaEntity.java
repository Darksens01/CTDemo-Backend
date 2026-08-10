package com.bracketops.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class MatchJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String tournamentId;

    @Column(nullable = false)
    private String round;

    private int matchOrder;

    private String teamAId;
    private String teamAName;
    private String teamBId;
    private String teamBName;

    private int teamAScore;
    private int teamBScore;

    private String winnerTeamId;
    private String winnerTeamName;

    @Column(nullable = false)
    private String status;

    private String nextMatchId;
    private String loserNextMatchId;

    @Version // Optimistic Locking concurrency protection
    private Long version;

    private LocalDateTime scheduledTime;

    public MatchJpaEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }
    public int getMatchOrder() { return matchOrder; }
    public void setMatchOrder(int matchOrder) { this.matchOrder = matchOrder; }
    public String getTeamAId() { return teamAId; }
    public void setTeamAId(String teamAId) { this.teamAId = teamAId; }
    public String getTeamAName() { return teamAName; }
    public void setTeamAName(String teamAName) { this.teamAName = teamAName; }
    public String getTeamBId() { return teamBId; }
    public void setTeamBId(String teamBId) { this.teamBId = teamBId; }
    public String getTeamBName() { return teamBName; }
    public void setTeamBName(String teamBName) { this.teamBName = teamBName; }
    public int getTeamAScore() { return teamAScore; }
    public void setTeamAScore(int teamAScore) { this.teamAScore = teamAScore; }
    public int getTeamBScore() { return teamBScore; }
    public void setTeamBScore(int teamBScore) { this.teamBScore = teamBScore; }
    public String getWinnerTeamId() { return winnerTeamId; }
    public void setWinnerTeamId(String winnerTeamId) { this.winnerTeamId = winnerTeamId; }
    public String getWinnerTeamName() { return winnerTeamName; }
    public void setWinnerTeamName(String winnerTeamName) { this.winnerTeamName = winnerTeamName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNextMatchId() { return nextMatchId; }
    public void setNextMatchId(String nextMatchId) { this.nextMatchId = nextMatchId; }
    public String getLoserNextMatchId() { return loserNextMatchId; }
    public void setLoserNextMatchId(String loserNextMatchId) { this.loserNextMatchId = loserNextMatchId; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
}

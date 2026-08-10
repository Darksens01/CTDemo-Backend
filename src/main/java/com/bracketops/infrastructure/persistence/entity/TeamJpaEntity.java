package com.bracketops.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class TeamJpaEntity {

    @Id
    private String id;

    private String tournamentId;

    @Column(nullable = false)
    private String teamName;

    private String logoUrl;

    @Column(nullable = false)
    private String captainUsername;

    @Column(nullable = false)
    private String status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "team_id")
    private List<PlayerJpaEntity> players = new ArrayList<>();

    private LocalDateTime createdAt;

    public TeamJpaEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getCaptainUsername() { return captainUsername; }
    public void setCaptainUsername(String captainUsername) { this.captainUsername = captainUsername; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<PlayerJpaEntity> getPlayers() { return players; }
    public void setPlayers(List<PlayerJpaEntity> players) { this.players = players; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

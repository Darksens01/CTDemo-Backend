package com.bracketops.domain.model.aggregate;

import com.bracketops.domain.model.entity.Player;
import com.bracketops.domain.model.exception.DomainException;
import com.bracketops.domain.model.valueobject.TeamStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Team {
    private final String id;
    private final String tournamentId;
    private final String teamName;
    private final String logoUrl;
    private final String captainUsername;
    private TeamStatus status;
    private final List<Player> players;
    private final LocalDateTime createdAt;

    public Team(String id, String tournamentId, String teamName, String logoUrl, String captainUsername,
                TeamStatus status, List<Player> players, LocalDateTime createdAt) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new DomainException("Team name cannot be empty");
        }
        if (captainUsername == null || captainUsername.trim().isEmpty()) {
            throw new DomainException("Captain username is required");
        }

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.tournamentId = tournamentId;
        this.teamName = teamName.trim();
        this.logoUrl = logoUrl != null ? logoUrl.trim() : "";
        this.captainUsername = captainUsername.trim();
        this.status = status != null ? status : TeamStatus.REGISTERED;
        this.players = players != null ? new ArrayList<>(players) : new ArrayList<>();
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void addPlayer(Player player) {
        if (player == null) throw new DomainException("Player cannot be null");
        this.players.add(player);
    }

    public void disqualify() {
        this.status = TeamStatus.DISQUALIFIED;
    }

    public void eliminate() {
        this.status = TeamStatus.ELIMINATED;
    }

    public String getId() { return id; }
    public String getTournamentId() { return tournamentId; }
    public String getTeamName() { return teamName; }
    public String getLogoUrl() { return logoUrl; }
    public String getCaptainUsername() { return captainUsername; }
    public TeamStatus getStatus() { return status; }
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

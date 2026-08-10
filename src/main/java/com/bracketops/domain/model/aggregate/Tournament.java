package com.bracketops.domain.model.aggregate;

import com.bracketops.domain.model.exception.DomainException;
import com.bracketops.domain.model.exception.InvalidTournamentStateException;
import com.bracketops.domain.model.valueobject.TournamentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Tournament {
    private final String id;
    private final String name;
    private final String gameName;
    private final String format;
    private final int maxTeams;
    private final int playersPerTeam;
    private TournamentStatus status;
    private final String prizePool;
    private final String bannerUrl;
    private String cancellationReason;
    private final List<String> registeredTeamIds;
    private String championTeamId;
    private String championTeamName;
    private final LocalDateTime startDate;
    private final LocalDateTime createdAt;

    public Tournament(String id, String name, String gameName, String format, int maxTeams, int playersPerTeam,
                      TournamentStatus status, String prizePool, String bannerUrl, String cancellationReason, List<String> registeredTeamIds,
                      String championTeamId, String championTeamName,
                      LocalDateTime startDate, LocalDateTime createdAt) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("Tournament name cannot be empty");
        }
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new DomainException("Game name cannot be empty");
        }

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name.trim();
        this.gameName = gameName.trim();
        this.format = format != null && !format.trim().isEmpty() ? format.trim() : "SINGLE_ELIMINATION";
        this.maxTeams = maxTeams > 0 ? maxTeams : 8;
        this.playersPerTeam = playersPerTeam > 0 ? playersPerTeam : 1;
        this.status = status != null ? status : TournamentStatus.DRAFT;
        this.prizePool = prizePool != null && !prizePool.trim().isEmpty() ? prizePool.trim() : "$500 USD";
        this.bannerUrl = bannerUrl;
        this.cancellationReason = cancellationReason;
        this.registeredTeamIds = registeredTeamIds != null ? new ArrayList<>(registeredTeamIds) : new ArrayList<>();
        this.championTeamId = championTeamId;
        this.championTeamName = championTeamName;
        this.startDate = startDate != null ? startDate : LocalDateTime.now().plusDays(1);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void cancelTournament(String reason) {
        if (this.status == TournamentStatus.COMPLETED) {
            throw new DomainException("Cannot cancel a completed tournament");
        }
        this.status = TournamentStatus.CANCELLED;
        this.cancellationReason = reason != null && !reason.trim().isEmpty() ? reason.trim() : "Motivo no especificado por la administración";
    }

    public void openRegistration() {
        if (this.status != TournamentStatus.DRAFT) {
            throw new InvalidTournamentStateException(this.name, this.status, "openRegistration");
        }
        this.status = TournamentStatus.REGISTRATION_OPEN;
    }

    public void registerTeam(String teamId) {
        if (this.status != TournamentStatus.REGISTRATION_OPEN) {
            throw new InvalidTournamentStateException(this.name, this.status, "registerTeam");
        }
        if (this.registeredTeamIds.contains(teamId)) {
            throw new DomainException("Team is already registered in this tournament");
        }
        if (this.registeredTeamIds.size() >= this.maxTeams) {
            throw new DomainException("Tournament registration is full (" + maxTeams + " teams maximum)");
        }
        this.registeredTeamIds.add(teamId);
    }

    public void removeTeamRegistration(String teamId) {
        if (this.status != TournamentStatus.REGISTRATION_OPEN && this.status != TournamentStatus.DRAFT) {
            throw new DomainException("Cannot remove team registration once brackets have been generated");
        }
        this.registeredTeamIds.remove(teamId);
    }

    public void markBracketGenerated() {
        if (this.status != TournamentStatus.REGISTRATION_OPEN) {
            throw new InvalidTournamentStateException(this.name, this.status, "generateBracket");
        }
        if (this.registeredTeamIds.size() < 2) {
            throw new DomainException("At least 2 registered teams are required to generate tournament brackets");
        }
        this.status = TournamentStatus.BRACKET_GENERATED;
    }

    public void startTournament() {
        this.status = TournamentStatus.IN_PROGRESS;
    }

    public void crownChampion(String teamId, String teamName) {
        this.championTeamId = teamId;
        this.championTeamName = teamName;
        this.status = TournamentStatus.COMPLETED;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getGameName() { return gameName; }
    public String getFormat() { return format; }
    public int getMaxTeams() { return maxTeams; }
    public int getPlayersPerTeam() { return playersPerTeam; }
    public TournamentStatus getStatus() { return status; }
    public String getPrizePool() { return prizePool; }
    public String getBannerUrl() { return bannerUrl; }
    public String getCancellationReason() { return cancellationReason; }
    public List<String> getRegisteredTeamIds() { return Collections.unmodifiableList(registeredTeamIds); }
    public String getChampionTeamId() { return championTeamId; }
    public String getChampionTeamName() { return championTeamName; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament tournament = (Tournament) o;
        return Objects.equals(id, tournament.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

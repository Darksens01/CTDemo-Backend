package com.bracketops.infrastructure.persistence.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
public class TournamentJpaEntity {

    @Id
    private String id;

    private String name;
    private String gameName;
    private String format;
    private int maxTeams;
    private int playersPerTeam;
    private String status;
    private String prizePool;
    private String bannerUrl;
    private String cancellationReason;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> registeredTeamIds = new ArrayList<>();

    private String championTeamId;
    private String championTeamName;

    private LocalDateTime startDate;
    private LocalDateTime createdAt;

    public TournamentJpaEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public int getMaxTeams() { return maxTeams; }
    public void setMaxTeams(int maxTeams) { this.maxTeams = maxTeams; }
    public int getPlayersPerTeam() { return playersPerTeam; }
    public void setPlayersPerTeam(int playersPerTeam) { this.playersPerTeam = playersPerTeam; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrizePool() { return prizePool; }
    public void setPrizePool(String prizePool) { this.prizePool = prizePool; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public List<String> getRegisteredTeamIds() { return registeredTeamIds; }
    public void setRegisteredTeamIds(List<String> registeredTeamIds) { this.registeredTeamIds = registeredTeamIds; }
    public String getChampionTeamId() { return championTeamId; }
    public void setChampionTeamId(String championTeamId) { this.championTeamId = championTeamId; }
    public String getChampionTeamName() { return championTeamName; }
    public void setChampionTeamName(String championTeamName) { this.championTeamName = championTeamName; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

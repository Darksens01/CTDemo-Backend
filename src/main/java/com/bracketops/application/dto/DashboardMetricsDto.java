package com.bracketops.application.dto;

public record DashboardMetricsDto(
    long totalTournaments,
    long activeTournaments,
    long completedTournaments,
    long totalTeams,
    long totalMatches,
    long completedMatches,
    long pushNotificationsSent
) {}

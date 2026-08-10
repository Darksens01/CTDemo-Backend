package com.bracketops.application.dto;

public record CreateTournamentCommand(
    String name,
    String gameName,
    String format,
    int maxTeams,
    int playersPerTeam,
    String prizePool,
    String startDateStr,
    String bannerUrl
) {}

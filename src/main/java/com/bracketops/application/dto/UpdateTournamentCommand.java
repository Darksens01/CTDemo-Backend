package com.bracketops.application.dto;

public record UpdateTournamentCommand(
    String id,
    String name,
    String gameName,
    String format,
    int maxTeams,
    int playersPerTeam,
    String prizePool,
    String bannerUrl
) {}

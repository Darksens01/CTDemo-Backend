package com.bracketops.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TournamentResponseDto(
    String id,
    String name,
    String gameName,
    String format,
    int maxTeams,
    int playersPerTeam,
    int registeredTeamsCount,
    List<String> registeredTeamIds,
    String status,
    String prizePool,
    String bannerUrl,
    String cancellationReason,
    String championTeamId,
    String championTeamName,
    LocalDateTime startDate,
    LocalDateTime createdAt
) {}

package com.bracketops.application.dto;

import java.time.LocalDateTime;

public record MatchResponseDto(
    String id,
    String tournamentId,
    String round,
    String roundDisplayName,
    int matchOrder,
    String teamAId,
    String teamAName,
    String teamBId,
    String teamBName,
    int teamAScore,
    int teamBScore,
    String winnerTeamId,
    String winnerTeamName,
    String status,
    String nextMatchId,
    String loserNextMatchId,
    Long version,
    LocalDateTime scheduledTime
) {}

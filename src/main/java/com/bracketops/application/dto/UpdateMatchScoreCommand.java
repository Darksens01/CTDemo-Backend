package com.bracketops.application.dto;

public record UpdateMatchScoreCommand(
    String matchId,
    int teamAScore,
    int teamBScore
) {}

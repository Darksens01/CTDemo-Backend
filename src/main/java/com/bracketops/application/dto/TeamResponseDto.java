package com.bracketops.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TeamResponseDto(
    String id,
    String tournamentId,
    String teamName,
    String logoUrl,
    String captainUsername,
    String status,
    List<PlayerResponseDto> players,
    LocalDateTime createdAt
) {
    public record PlayerResponseDto(String id, String gamerTag, String realName, String inGameRole) {}
}

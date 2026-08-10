package com.bracketops.application.dto;

import java.util.List;

public record RegisterTeamCommand(
    String tournamentId,
    String teamName,
    String logoUrl,
    String captainUsername,
    List<PlayerDto> players
) {
    public record PlayerDto(String gamerTag, String realName, String inGameRole) {}
}

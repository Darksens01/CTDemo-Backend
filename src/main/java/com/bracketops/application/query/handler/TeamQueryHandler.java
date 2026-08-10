package com.bracketops.application.query.handler;

import com.bracketops.application.dto.TeamResponseDto;
import com.bracketops.domain.model.aggregate.Team;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class TeamQueryHandler {

    private final TeamRepositoryPort teamRepositoryPort;

    public TeamQueryHandler(TeamRepositoryPort teamRepositoryPort) {
        this.teamRepositoryPort = teamRepositoryPort;
    }

    public TeamResponseDto getById(String id) {
        Team t = teamRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return mapToDto(t);
    }

    public List<TeamResponseDto> getAll() {
        return teamRepositoryPort.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private TeamResponseDto mapToDto(Team team) {
        List<TeamResponseDto.PlayerResponseDto> playerDtos = team.getPlayers().stream()
                .map(p -> new TeamResponseDto.PlayerResponseDto(p.getId(), p.getGamerTag(), p.getRealName(), p.getInGameRole()))
                .collect(Collectors.toList());

        return new TeamResponseDto(
                team.getId(),
                team.getTournamentId(),
                team.getTeamName(),
                team.getLogoUrl(),
                team.getCaptainUsername(),
                team.getStatus().name(),
                playerDtos,
                team.getCreatedAt()
        );
    }
}

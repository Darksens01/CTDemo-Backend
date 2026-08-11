package com.bracketops.application.command.handler;

import com.bracketops.application.dto.RegisterTeamCommand;
import com.bracketops.application.dto.TeamResponseDto;
import com.bracketops.domain.model.aggregate.Team;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.entity.Player;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.model.valueobject.TeamStatus;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterTeamCommandHandler {

    private final TeamRepositoryPort teamRepositoryPort;
    private final TournamentRepositoryPort tournamentRepositoryPort;

    public RegisterTeamCommandHandler(TeamRepositoryPort teamRepositoryPort,
            TournamentRepositoryPort tournamentRepositoryPort) {
        this.teamRepositoryPort = teamRepositoryPort;
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    public TeamResponseDto handle(RegisterTeamCommand command) {
        Tournament tournament = tournamentRepositoryPort.findById(command.tournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", command.tournamentId()));

        List<Player> players = new ArrayList<>();
        if (command.players() != null) {
            players = command.players().stream()
                    .map(p -> new Player(null, p.gamerTag(), p.realName(), p.inGameRole()))
                    .collect(Collectors.toList());
        }

        Team team = new Team(
                null,
                command.tournamentId(),
                command.teamName(),
                command.logoUrl(),
                command.captainUsername(),
                TeamStatus.REGISTERED,
                players,
                null);

        Team savedTeam = teamRepositoryPort.save(team);
        tournament.registerTeam(savedTeam.getId());
        tournamentRepositoryPort.save(tournament);

        return mapToDto(savedTeam);
    }

    private TeamResponseDto mapToDto(Team team) {
        List<TeamResponseDto.PlayerResponseDto> playerDtos = team.getPlayers().stream()
                .map(p -> new TeamResponseDto.PlayerResponseDto(p.getId(), p.getGamerTag(), p.getRealName(),
                        p.getInGameRole()))
                .collect(Collectors.toList());

        return new TeamResponseDto(
                team.getId(),
                team.getTournamentId(),
                team.getTeamName(),
                team.getLogoUrl(),
                team.getCaptainUsername(),
                team.getStatus().name(),
                playerDtos,
                team.getCreatedAt());
    }
}

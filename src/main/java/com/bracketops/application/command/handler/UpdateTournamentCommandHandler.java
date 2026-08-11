package com.bracketops.application.command.handler;

import com.bracketops.application.dto.TournamentResponseDto;
import com.bracketops.application.dto.UpdateTournamentCommand;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.exception.DomainException;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;

public class UpdateTournamentCommandHandler {

    private final TournamentRepositoryPort tournamentRepositoryPort;

    public UpdateTournamentCommandHandler(TournamentRepositoryPort tournamentRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    public TournamentResponseDto handle(UpdateTournamentCommand command) {
        Tournament existing = tournamentRepositoryPort.findById(command.id())
                .orElseThrow(() -> new DomainException("Tournament not found with ID: " + command.id()));

        Tournament updated = new Tournament(
                existing.getId(),
                command.name() != null && !command.name().trim().isEmpty() ? command.name() : existing.getName(),
                command.gameName() != null && !command.gameName().trim().isEmpty() ? command.gameName() : existing.getGameName(),
                command.format() != null && !command.format().trim().isEmpty() ? command.format() : existing.getFormat(),
                command.maxTeams() > 0 ? command.maxTeams() : existing.getMaxTeams(),
                command.playersPerTeam() > 0 ? command.playersPerTeam() : existing.getPlayersPerTeam(),
                existing.getStatus(),
                command.prizePool() != null && !command.prizePool().trim().isEmpty() ? command.prizePool() : existing.getPrizePool(),
                command.bannerUrl() != null ? command.bannerUrl() : existing.getBannerUrl(),
                existing.getCancellationReason(),
                existing.getRegisteredTeamIds(),
                existing.getChampionTeamId(),
                existing.getChampionTeamName(),
                existing.getStartDate(),
                existing.getCreatedAt()
        );

        Tournament saved = tournamentRepositoryPort.save(updated);
        return mapToDto(saved);
    }

    private TournamentResponseDto mapToDto(Tournament tournament) {
        return new TournamentResponseDto(
                tournament.getId(),
                tournament.getName(),
                tournament.getGameName(),
                tournament.getFormat(),
                tournament.getMaxTeams(),
                tournament.getPlayersPerTeam(),
                tournament.getRegisteredTeamIds().size(),
                tournament.getRegisteredTeamIds(),
                tournament.getStatus().name(),
                tournament.getPrizePool(),
                tournament.getBannerUrl(),
                tournament.getCancellationReason(),
                tournament.getChampionTeamId(),
                tournament.getChampionTeamName(),
                tournament.getStartDate(),
                tournament.getCreatedAt()
        );
    }
}

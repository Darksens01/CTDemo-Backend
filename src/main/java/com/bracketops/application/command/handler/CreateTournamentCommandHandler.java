package com.bracketops.application.command.handler;

import com.bracketops.application.dto.CreateTournamentCommand;
import com.bracketops.application.dto.TournamentResponseDto;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.valueobject.TournamentStatus;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;

import java.time.LocalDateTime;

public class CreateTournamentCommandHandler {

    private final TournamentRepositoryPort tournamentRepositoryPort;

    public CreateTournamentCommandHandler(TournamentRepositoryPort tournamentRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    public TournamentResponseDto handle(CreateTournamentCommand command) {
        Tournament tournament = new Tournament(
                null,
                command.name(),
                command.gameName(),
                command.format() != null ? command.format() : "SINGLE_ELIMINATION",
                command.maxTeams() > 0 ? command.maxTeams() : 8,
                command.playersPerTeam() > 0 ? command.playersPerTeam() : 1,
                TournamentStatus.REGISTRATION_OPEN,
                command.prizePool() != null ? command.prizePool() : "$500 USD",
                command.bannerUrl(),
                null, null, null, null,
                LocalDateTime.now().plusDays(2),
                null
        );

        Tournament saved = tournamentRepositoryPort.save(tournament);
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

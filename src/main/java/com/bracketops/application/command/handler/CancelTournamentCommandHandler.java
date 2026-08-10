package com.bracketops.application.command.handler;

import com.bracketops.application.dto.CancelTournamentCommand;
import com.bracketops.application.dto.TournamentResponseDto;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.port.outbound.NotificationPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;

public class CancelTournamentCommandHandler {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final NotificationPort notificationPort;

    public CancelTournamentCommandHandler(TournamentRepositoryPort tournamentRepositoryPort,
                                        NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.notificationPort = notificationPort;
    }

    public TournamentResponseDto handle(CancelTournamentCommand command) {
        Tournament tournament = tournamentRepositoryPort.findById(command.tournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", command.tournamentId()));

        tournament.cancelTournament(command.reason());
        Tournament saved = tournamentRepositoryPort.save(tournament);

        notificationPort.sendTournamentCancelledNotification(saved.getName(), command.reason());

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

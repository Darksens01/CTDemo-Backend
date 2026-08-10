package com.bracketops.application.query.handler;

import com.bracketops.application.dto.TournamentResponseDto;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class TournamentQueryHandler {

    private final TournamentRepositoryPort tournamentRepositoryPort;

    public TournamentQueryHandler(TournamentRepositoryPort tournamentRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    public TournamentResponseDto getById(String id) {
        Tournament t = tournamentRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
        return mapToDto(t);
    }

    public List<TournamentResponseDto> getAll() {
        return tournamentRepositoryPort.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
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

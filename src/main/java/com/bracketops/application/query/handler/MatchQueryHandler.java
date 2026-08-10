package com.bracketops.application.query.handler;

import com.bracketops.application.dto.MatchResponseDto;
import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class MatchQueryHandler {

    private final MatchRepositoryPort matchRepositoryPort;

    public MatchQueryHandler(MatchRepositoryPort matchRepositoryPort) {
        this.matchRepositoryPort = matchRepositoryPort;
    }

    public MatchResponseDto getById(String id) {
        Match m = matchRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
        return mapToDto(m);
    }

    public List<MatchResponseDto> getByTournamentId(String tournamentId) {
        return matchRepositoryPort.findByTournamentId(tournamentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MatchResponseDto mapToDto(Match match) {
        return new MatchResponseDto(
                match.getId(),
                match.getTournamentId(),
                match.getRound().name(),
                match.getRound().getDisplayName(),
                match.getMatchOrder(),
                match.getTeamAId(),
                match.getTeamAName(),
                match.getTeamBId(),
                match.getTeamBName(),
                match.getTeamAScore(),
                match.getTeamBScore(),
                match.getWinnerTeamId(),
                match.getWinnerTeamName(),
                match.getStatus().name(),
                match.getNextMatchId(),
                match.getLoserNextMatchId(),
                match.getVersion(),
                match.getScheduledTime()
        );
    }
}

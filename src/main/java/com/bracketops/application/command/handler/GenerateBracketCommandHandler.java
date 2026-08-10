package com.bracketops.application.command.handler;

import com.bracketops.application.dto.MatchResponseDto;
import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.aggregate.Team;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;
import com.bracketops.domain.service.BracketGeneratorEngine;

import java.util.List;
import java.util.stream.Collectors;

public class GenerateBracketCommandHandler {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;
    private final MatchRepositoryPort matchRepositoryPort;
    private final BracketGeneratorEngine bracketEngine;

    public GenerateBracketCommandHandler(TournamentRepositoryPort tournamentRepositoryPort,
                                         TeamRepositoryPort teamRepositoryPort,
                                         MatchRepositoryPort matchRepositoryPort,
                                         BracketGeneratorEngine bracketEngine) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
        this.matchRepositoryPort = matchRepositoryPort;
        this.bracketEngine = bracketEngine;
    }

    public List<MatchResponseDto> handle(String tournamentId) {
        Tournament tournament = tournamentRepositoryPort.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", tournamentId));

        tournament.markBracketGenerated();

        List<Team> teams = teamRepositoryPort.findByIds(tournament.getRegisteredTeamIds());
        List<Match> matches = bracketEngine.generateBracket(tournament, teams);

        tournament.startTournament();
        tournamentRepositoryPort.save(tournament);
        List<Match> savedMatches = matchRepositoryPort.saveAll(matches);

        return savedMatches.stream().map(this::mapToDto).collect(Collectors.toList());
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

package com.bracketops.application.query.handler;

import com.bracketops.application.dto.DashboardMetricsDto;
import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.valueobject.MatchStatus;
import com.bracketops.domain.model.valueobject.TournamentStatus;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;

import java.util.List;

public class DashboardQueryHandler {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;
    private final MatchRepositoryPort matchRepositoryPort;

    public DashboardQueryHandler(TournamentRepositoryPort tournamentRepositoryPort,
                                 TeamRepositoryPort teamRepositoryPort,
                                 MatchRepositoryPort matchRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
        this.matchRepositoryPort = matchRepositoryPort;
    }

    public DashboardMetricsDto getMetrics() {
        List<Tournament> tournaments = tournamentRepositoryPort.findAll();
        long totalTournaments = tournaments.size();
        long activeTournaments = tournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.IN_PROGRESS || t.getStatus() == TournamentStatus.BRACKET_GENERATED)
                .count();
        long completedTournaments = tournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.COMPLETED)
                .count();

        long totalTeams = teamRepositoryPort.findAll().size();

        long totalMatches = 0;
        long completedMatches = 0;
        for (Tournament t : tournaments) {
            List<Match> matches = matchRepositoryPort.findByTournamentId(t.getId());
            totalMatches += matches.size();
            completedMatches += matches.stream()
                    .filter(m -> m.getStatus() == MatchStatus.COMPLETED || m.getStatus() == MatchStatus.WALKOVER)
                    .count();
        }

        return new DashboardMetricsDto(
                totalTournaments,
                activeTournaments,
                completedTournaments,
                totalTeams,
                totalMatches,
                completedMatches,
                completedMatches // Each completed match emits a push notification
        );
    }
}

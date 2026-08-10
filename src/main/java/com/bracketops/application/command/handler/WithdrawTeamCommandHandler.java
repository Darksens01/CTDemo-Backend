package com.bracketops.application.command.handler;

import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.aggregate.Team;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.model.valueobject.TournamentStatus;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;
import com.bracketops.domain.port.outbound.NotificationPort;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;
import com.bracketops.domain.service.MatchAdvancementEngine;

import java.util.List;

public class WithdrawTeamCommandHandler {

    private final TeamRepositoryPort teamRepositoryPort;
    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final MatchRepositoryPort matchRepositoryPort;
    private final NotificationPort notificationPort;
    private final MatchAdvancementEngine advancementEngine;

    public WithdrawTeamCommandHandler(TeamRepositoryPort teamRepositoryPort,
                                      TournamentRepositoryPort tournamentRepositoryPort,
                                      MatchRepositoryPort matchRepositoryPort,
                                      NotificationPort notificationPort,
                                      MatchAdvancementEngine advancementEngine) {
        this.teamRepositoryPort = teamRepositoryPort;
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.matchRepositoryPort = matchRepositoryPort;
        this.notificationPort = notificationPort;
        this.advancementEngine = advancementEngine;
    }

    public void handle(String tournamentId, String teamId) {
        Tournament tournament = tournamentRepositoryPort.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", tournamentId));

        Team team = teamRepositoryPort.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));

        // Case 1: Pre-bracket generation -> Hard remove registration
        if (tournament.getStatus() == TournamentStatus.REGISTRATION_OPEN || tournament.getStatus() == TournamentStatus.DRAFT) {
            tournament.removeTeamRegistration(teamId);
            tournamentRepositoryPort.save(tournament);
            return;
        }

        // Case 2: Post-bracket generation -> Disqualify team & execute Walkover W.O. (2-0)
        team.disqualify();
        teamRepositoryPort.save(team);

        List<Match> matches = matchRepositoryPort.findByTournamentId(tournamentId);
        for (Match match : matches) {
            if (teamId.equals(match.getTeamAId()) || teamId.equals(match.getTeamBId())) {
                match.processWalkover(teamId);
                Match saved = matchRepositoryPort.save(match);

                notificationPort.sendMatchUpdateNotification(
                        tournament.getName(),
                        saved.getRound().getDisplayName(),
                        saved.getWinnerTeamName(),
                        team.getTeamName() + " (DISQUALIFIED)",
                        2, 0
                );

                if (saved.getNextMatchId() != null) {
                    matchRepositoryPort.findById(saved.getNextMatchId()).ifPresent(nextMatch -> {
                        advancementEngine.advanceWinnerToNextMatch(saved, nextMatch);
                        matchRepositoryPort.save(nextMatch);
                    });
                }

                if (saved.getLoserNextMatchId() != null) {
                    matchRepositoryPort.findById(saved.getLoserNextMatchId()).ifPresent(loserMatch -> {
                        advancementEngine.advanceLoserToNextMatch(saved, loserMatch);
                        matchRepositoryPort.save(loserMatch);
                    });
                }
            }
        }
    }
}

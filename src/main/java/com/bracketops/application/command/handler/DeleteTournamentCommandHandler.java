package com.bracketops.application.command.handler;

import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.exception.InvalidTournamentStateException;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.model.valueobject.TournamentStatus;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;

public class DeleteTournamentCommandHandler {

    private final TournamentRepositoryPort tournamentRepositoryPort;

    public DeleteTournamentCommandHandler(TournamentRepositoryPort tournamentRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    public void handle(String tournamentId) {
        Tournament tournament = tournamentRepositoryPort.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", tournamentId));

        if (tournament.getStatus() != TournamentStatus.CANCELLED) {
            throw new InvalidTournamentStateException(tournament.getName(), tournament.getStatus(), "DELETE");
        }

        tournamentRepositoryPort.deleteById(tournamentId);
    }
}

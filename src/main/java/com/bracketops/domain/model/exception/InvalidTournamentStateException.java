package com.bracketops.domain.model.exception;

import com.bracketops.domain.model.valueobject.TournamentStatus;

public class InvalidTournamentStateException extends DomainException {
    public InvalidTournamentStateException(String tournamentName, TournamentStatus currentStatus, String allowedOperation) {
        super("Operation '" + allowedOperation + "' denied for tournament '" + tournamentName + "'. Current status is " + currentStatus + ".");
    }
}

package com.bracketops.domain.model.valueobject;

public enum MatchRound {
    ROUND_OF_16("Round of 16"),
    QUARTERFINALS("Quarterfinals"),
    SEMIFINALS("Semifinals"),
    WINNERS_FINALS("Winners Final"),
    LOSERS_ROUND_1("Losers Round 1"),
    LOSERS_FINALS("Losers Final"),
    GRAND_FINALS("Grand Finals"),
    ROUND_ROBIN("Jornada de Liga"),
    SWISS_ROUND_1("Ronda Suizo"),
    FFA_ROUND_1("Ronda Batalla Real");

    private final String displayName;

    MatchRound(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
